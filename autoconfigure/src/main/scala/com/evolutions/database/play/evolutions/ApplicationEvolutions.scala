/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database.play.evolutions

import java.sql.{Connection, SQLException, Statement}

import com.evolutions.database.autoconfigure.DatabaseEvolutionsConf
import com.evolutions.database.exceptions.PlayException
import com.evolutions.database.play._
import com.evolutions.database.play.db.{Database, MyDatabase}
import com.evolutions.database.play.evolutions.DatabaseUrlPatterns._
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.util.control.Exception.ignoring

/**
 * Run evolutions on application startup. Automatically runs on construction.
 */
@Component
class ApplicationEvolutions(
    reader: EvolutionsReader,
    evolutions: EvolutionsApi,
    dbApi: MyDatabase,
    config: DatabaseEvolutionsConf
) {

  private val logger = LoggerFactory.getLogger(classOf[ApplicationEvolutions])

  private var invalidDatabaseRevisions = 0

  /**
   * Indicates if the process of applying evolutions scripts is finished or not.
   * Only if that method returns true you can be sure that all evolutions scripts were executed successfully.
   *
   * @return true if all evolutions scripts were applied (or resolved) successfully.
   */
  def upToDate = invalidDatabaseRevisions == 0

  /**
   * Checks the evolutions state. Called on construction.
   */
  def start(): Unit = {

    dbApi
      .databases()
      .foreach(
        ApplicationEvolutions.runEvolutions(
          _,
          config,
          evolutions,
          reader,
          (db, dbConfig, schema, scripts, hasDown, autocommit) => {
            import Evolutions.toHumanReadableScript

            def invalidDatabaseRevision() = {
              invalidDatabaseRevisions += 1
              throw InvalidDatabaseRevision(db, toHumanReadableScript(scripts))
            }

            com.evolutions.database.Mode.valueOf(config.getMode.toUpperCase).asScala() match {
              case Mode.Test => {
                logger.info(s"Apply evolutions in [$db]")
                evolutions.evolve(db, scripts, autocommit, schema)
              }
              case Mode.Dev =>  {
                logger.info(s"Apply evolutions in [$db]")
                evolutions.evolve(db, scripts, autocommit, schema)
              }
              case Mode.Prod if !hasDown && dbConfig.isAutoApply => evolutions.evolve(db, scripts, autocommit, schema)
              case Mode.Prod if hasDown && dbConfig.isAutoApply && dbConfig.isAutoApplyDowns =>
                evolutions.evolve(db, scripts, autocommit, schema)
              case Mode.Prod if hasDown =>
                logger.warn(
                  s"Your production database [$db] needs evolutions, including downs! \n\n${toHumanReadableScript(scripts)}"
                )
                logger.warn(
                  s"Run with -Dplay.evolutions.db.$db.autoApply=true and -Dplay.evolutions.db.$db.autoApplyDowns=true if you want to run them automatically, including downs (be careful, especially if your down evolutions drop existing data)"
                )

                invalidDatabaseRevision()

              case Mode.Prod =>
                logger.warn(s"Your production database [$db] needs evolutions! \n\n${toHumanReadableScript(scripts)}")
                logger.warn(
                  s"Run with -Dplay.evolutions.db.$db.autoApply=true if you want to run them automatically (be careful)"
                )

                invalidDatabaseRevision()

              case _ =>
                invalidDatabaseRevision()
            }
          }
        )
      )
  }

//  start() // on construction
}

private object ApplicationEvolutions {

  private val logger = LoggerFactory.getLogger(classOf[ApplicationEvolutions])

  val SelectPlayEvolutionsLockSql =
    """
      select lock from ${schema}play_evolutions_lock
    """

  val SelectPlayEvolutionsLockMysqlSql =
    """
      select `lock` from ${schema}play_evolutions_lock
    """

  val SelectPlayEvolutionsLockOracleSql =
    """
      select "lock" from ${schema}play_evolutions_lock
    """

  val CreatePlayEvolutionsLockSql =
    """
      create table ${schema}play_evolutions_lock (
        lock int not null primary key
      )
    """

  val CreatePlayEvolutionsLockMysqlSql =
    """
      create table ${schema}play_evolutions_lock (
        `lock` int not null primary key
      )
    """

  val CreatePlayEvolutionsLockOracleSql =
    """
      CREATE TABLE ${schema}play_evolutions_lock (
        "lock" Number(10,0) Not Null Enable,
        CONSTRAINT play_evolutions_lock_pk PRIMARY KEY ("lock")
      )
    """

  val InsertIntoPlayEvolutionsLockSql =
    """
      insert into ${schema}play_evolutions_lock (lock) values (1)
    """

  val InsertIntoPlayEvolutionsLockMysqlSql =
    """
      insert into ${schema}play_evolutions_lock (`lock`) values (1)
    """

  val InsertIntoPlayEvolutionsLockOracleSql =
    """
      insert into ${schema}play_evolutions_lock ("lock") values (1)
    """

  val lockPlayEvolutionsLockSqls =
    List(
      """
        select lock from ${schema}play_evolutions_lock where lock = 1 for update nowait
      """
    )

  val lockPlayEvolutionsLockMysqlSqls =
    List(
      """
        set innodb_lock_wait_timeout = 1
      """,
      """
        select `lock` from ${schema}play_evolutions_lock where `lock` = 1 for update
      """
    )

  val lockPlayEvolutionsLockOracleSqls =
    List(
      """
        select "lock" from ${schema}play_evolutions_lock where "lock" = 1 for update nowait
      """
    )

  def runEvolutions(
                     database: Database,
                     dbConfig: DatabaseEvolutionsConf,
                     evolutions: EvolutionsApi,
                     reader: EvolutionsReader,
                     block: (String, DatabaseEvolutionsConf, String, Seq[Script], Boolean, Boolean) => Unit
  ): Unit = {
    val db       = database.name
    if (dbConfig.isEnable) {
      withLock(database, dbConfig) {
        val schema     = dbConfig.getSchema
        val autocommit = dbConfig.isAutocommit

        val scripts   = evolutions.scripts(db, reader, schema)
        val hasDown   = scripts.exists(_.isInstanceOf[DownScript])
        val onlyDowns = scripts.forall(_.isInstanceOf[DownScript])

        if (scripts.nonEmpty && !(onlyDowns && dbConfig.isSkipApplyDownsOnly)) {
          block.apply(db, dbConfig, schema, scripts, hasDown, autocommit)
        }
      }
    }
  }

  private def withLock(db: Database, dbConfig: DatabaseEvolutionsConf)(block: => Unit): Unit = {
    if (dbConfig.isUseLocks) {
      val ds  = db.dataSource
      val url = db.url
      val c   = ds.getConnection
      c.setAutoCommit(false)
      val s = c.createStatement()
      createLockTableIfNecessary(url, c, s, dbConfig)
      lock(url, c, s, dbConfig)
      try {
        block
      } finally {
        unlock(c, s)
      }
    } else {
      block
    }
  }

  private def createLockTableIfNecessary(
      url: String,
      c: Connection,
      s: Statement,
      dbConfig: DatabaseEvolutionsConf
  ): Unit = {
    val (selectScript, createScript, insertScript) = url match {
      case OracleJdbcUrl() =>
        (SelectPlayEvolutionsLockOracleSql, CreatePlayEvolutionsLockOracleSql, InsertIntoPlayEvolutionsLockOracleSql)
      case MysqlJdbcUrl(_) =>
        (SelectPlayEvolutionsLockMysqlSql, CreatePlayEvolutionsLockMysqlSql, InsertIntoPlayEvolutionsLockMysqlSql)
      case _ =>
        (SelectPlayEvolutionsLockSql, CreatePlayEvolutionsLockSql, InsertIntoPlayEvolutionsLockSql)
    }
    try {
      val r = s.executeQuery(applySchema(selectScript, dbConfig.getSchema))
      r.close()
    } catch {
      case e: SQLException =>
        c.rollback()
        s.execute(applySchema(createScript, dbConfig.getSchema))
        s.executeUpdate(applySchema(insertScript, dbConfig.getSchema))
    }
  }

  private def lock(
                    url: String,
                    c: Connection,
                    s: Statement,
                    dbConfig: DatabaseEvolutionsConf,
                    attempts: Int = 5
  ): Unit = {
    val lockScripts = url match {
      case MysqlJdbcUrl(_) => lockPlayEvolutionsLockMysqlSqls
      case OracleJdbcUrl() => lockPlayEvolutionsLockOracleSqls
      case _               => lockPlayEvolutionsLockSqls
    }
    try {
      for (script <- lockScripts) s.executeQuery(applySchema(script, dbConfig.getSchema))
    } catch {
      case e: SQLException =>
        if (attempts == 0) throw e
        else {
          logger.warn(
            "Exception while attempting to lock evolutions (other node probably has lock), sleeping for 1 sec"
          )
          c.rollback()
          Thread.sleep(1000)
          lock(url, c, s, dbConfig, attempts - 1)
        }
    }
  }

  private def unlock(c: Connection, s: Statement): Unit = {
    ignoring(classOf[SQLException])(s.close())
    ignoring(classOf[SQLException])(c.commit())
    ignoring(classOf[SQLException])(c.close())
  }

  // SQL helpers

  private def applySchema(sql: String, schema: String): String = {
    sql.replaceAll("\\$\\{schema}", Option(schema).filter(_.trim.nonEmpty).map(_.trim + ".").getOrElse(""))
  }

}

/**
 * Evolutions configuration for a given datasource.
 */
trait EvolutionsDatasourceConfig {
  def enabled: Boolean
  def schema: String
  def autocommit: Boolean
  def useLocks: Boolean
  def autoApply: Boolean
  def autoApplyDowns: Boolean
  def skipApplyDownsOnly: Boolean
}

/**
 * Evolutions configuration for all datasources.
 */
trait EvolutionsConfig {
  def forDatasource(db: String): EvolutionsDatasourceConfig
}

/**
 * Default evolutions datasource configuration.
 */
case class DefaultEvolutionsDatasourceConfig(
    enabled: Boolean,
    schema: String,
    autocommit: Boolean,
    useLocks: Boolean,
    autoApply: Boolean,
    autoApplyDowns: Boolean,
    skipApplyDownsOnly: Boolean
) extends EvolutionsDatasourceConfig

/**
 * Default evolutions configuration.
 */
class DefaultEvolutionsConfig(
    defaultDatasourceConfig: EvolutionsDatasourceConfig,
    datasources: Map[String, EvolutionsDatasourceConfig]
) extends EvolutionsConfig {
  def forDatasource(db: String) = datasources.getOrElse(db, defaultDatasourceConfig)
}

/**
 * Web command handler for applying evolutions on application start.
 */
//@Component
class EvolutionsWebCommands(
                             @Autowired private val dbApi: MyDatabase,
                             @Autowired private val evolutions: EvolutionsApi,
                             @Autowired private val reader: EvolutionsReader,
                             @Autowired private val config: DatabaseEvolutionsConf,
) {
  var checkedAlready = false

  val applyEvolutions   = """/@evolutions/apply/([a-zA-Z0-9_-]+)""".r
  val resolveEvolutions = """/@evolutions/resolve/([a-zA-Z0-9_-]+)/([0-9]+)""".r

  lazy val redirectUrl = ""

  // Regex removes all parent directories from request path
  "".replaceFirst("^((?!/@evolutions).)*(/@evolutions.*$)", "$2") match {

    case applyEvolutions(db) => {
      Some {
        val scripts = evolutions.scripts(db, reader, config.getSchema)
        evolutions.evolve(db, scripts, config.isAutocommit, config.getSchema)
      }
    }

    case resolveEvolutions(db, rev) => {
      Some {
        evolutions.resolve(db, rev.toInt, config.getSchema)
      }
    }

    case _ => {
      synchronized {
        var autoApplyCount = 0
        if (!checkedAlready) {
          dbApi
            .databases()
            .foreach(
              ApplicationEvolutions.runEvolutions(
                _,
                config,
                evolutions,
                reader,
                (db, dbConfig, schema, scripts, hasDown, autocommit) => {
                  import Evolutions.toHumanReadableScript

                  if (dbConfig.isAutoApply) {
                    evolutions.evolve(db, scripts, autocommit, schema)
                    autoApplyCount += 1
                  } else {
                    throw InvalidDatabaseRevision(db, toHumanReadableScript(scripts))
                  }
                }
              )
            )
          checkedAlready = true
          if (autoApplyCount > 0) {
//            buildLink.forceReload()
          }
        }
      }
      None
    }
  }
}

/**
 * Exception thrown when the database is not up to date.
 *
 * @param db the database name
 * @param script the script to be run to resolve the conflict.
 */
case class InvalidDatabaseRevision(db: String, script: String)
    extends PlayException.RichDescription(
      "Database '" + db + "' needs evolution!",
      "An SQL script need to be run on your database."
    ) {

  def subTitle = "This SQL script must be run:"
  def content  = script

  private val javascript =
    """
        window.location = window.location.href.split(/[?#]/)[0].replace(/\/@evolutions.*$|\/$/, '') + '/@evolutions/apply/%s?redirect=' + encodeURIComponent(location)
    """.format(db).trim

  def htmlDescription = {

    <span>An SQL script will be run on your database -</span>
    <input name="evolution-button" type="button" value="Apply this script now!" onclick={javascript}/>

  }.mkString

}
