/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database.play.db

import java.sql.Connection

import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.util.control.ControlThrowable

/**
 * Creation helpers for manually instantiating databases.
 */
class Databases (@Autowired private val database: MyDatabase) {

  /**
   * Create a pooled database named "default" with the given driver and url.
   *
   * @param driver the database driver class
   * @param url the database url
   * @param name the database name
   * @param config a map of extra database configuration
   * @return a configured database
   */
  def apply( ): Database = {
    database
  }

  /**
   * Create an in-memory H2 database.
   *
   * @param name the database name (defaults to "default")
   * @param urlOptions a map of extra url options
   * @param config a map of extra database configuration
   * @return a configured in-memory h2 database
   */
//  def inMemory(
//      name: String = "default",
//      urlOptions: Map[String, String] = Map.empty,
//      config: Map[String, _ <: Any] = Map.empty
//  ): Database = {
//    val driver   = "org.h2.Driver"
//    val urlExtra = if (urlOptions.nonEmpty) urlOptions.map { case (k, v) => k + "=" + v }.mkString(";", ";", "") else ""
//    val url      = "jdbc:h2:mem:" + name + urlExtra
//    Databases()
//  }

  /**
   * Run the given block with a database, cleaning up afterwards.
   *
   * @param driver the database driver class
   * @param url the database url
   * @param name the database name
   * @param config a map of extra database configuration
   * @param block The block of code to run
   * @return The result of the block
   */
//  def withDatabase[T](driver: String, url: String, name: String = "default", config: Map[String, _ <: Any] = Map.empty)(
//      block: Database => T
//  ): T = {
//    val database = Databases(driver, url, name, config)
//    try {
//      block(database)
//    } finally {
//      database.shutdown()
//    }
//  }

  /**
   * Run the given block with an in-memory h2 database, cleaning up afterwards.
   *
   * @param name the database name (defaults to "default")
   * @param urlOptions a map of extra url options
   * @param config a map of extra database configuration
   * @param block The block of code to run
   * @return The result of the block
   */
//  def withInMemory[T](
//      name: String = "default",
//      urlOptions: Map[String, String] = Map.empty,
//      config: Map[String, _ <: Any] = Map.empty
//  )(block: Database => T): T = {
//    val database = inMemory(name, urlOptions, config)
//    try {
//      block(database)
//    } finally {
//      database.shutdown()
//    }
//  }
}

/**
 * Default implementation of the database API.
 * Provides driver registration and connection methods.
 */
@Service
abstract class DefaultDatabase(val name: String) extends Database {

  // abstract methods to be implemented

  def createDataSource(): DataSource

  def closeDataSource(dataSource: DataSource): Unit

  // lazy data source creation

  lazy val dataSource: DataSource = {
    createDataSource()
  }

  lazy val url: String = {
    val connection = dataSource.getConnection
    try {
      connection.getMetaData.getURL
    } finally {
      connection.close()
    }
  }

  // connection methods

  def getConnection(): Connection = {
    getConnection(autocommit = true)
  }

  def getConnection(autocommit: Boolean): Connection = {
    val connection = dataSource.getConnection
    try {
      connection.setAutoCommit(autocommit)
    } catch {
      case e: Throwable =>
        connection.close()
        throw e
    }
    connection
  }

  def withConnection[A](block: Connection => A): A = {
    withConnection(autocommit = true)(block)
  }

  def withConnection[A](autocommit: Boolean)(block: Connection => A): A = {
    val connection = getConnection(autocommit)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }

  def withTransaction[A](block: Connection => A): A = {
    withConnection(autocommit = false) { connection =>
      try {
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: ControlThrowable =>
          connection.commit()
          throw e
        case e: Throwable =>
          connection.rollback()
          throw e
      }
    }
  }

  def withTransaction[A](isolationLevel: TransactionIsolationLevel)(block: Connection => A): A = {
    withConnection(autocommit = false) { connection =>
      val oldIsolationLevel = connection.getTransactionIsolation
      try {
        connection.setTransactionIsolation(isolationLevel.id)
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: ControlThrowable =>
          connection.commit()
          throw e
        case e: Throwable =>
          connection.rollback()
          throw e
      } finally {
        connection.setTransactionIsolation(oldIsolationLevel)
      }
    }
  }

  // shutdown

  def shutdown(): Unit = {
    closeDataSource(dataSource)
  }

}

@Service
class MyDatabase(@Autowired private val ds: DataSource) extends Database {

  def createDataSource(): DataSource = ds

  def closeDataSource(dataSource: DataSource): Unit = dataSource.getConnection.close()

  // lazy data source creation

  lazy val dataSource: DataSource = {
    createDataSource()
  }

  lazy val url: String = {
    val connection = dataSource.getConnection
    try {
      connection.getMetaData.getURL
    } finally {
      connection.close()
    }
  }

  // connection methods

  def getConnection(): Connection = {
    getConnection(autocommit = true)
  }

  def getConnection(autocommit: Boolean): Connection = {
    val connection = dataSource.getConnection
    try {
      connection.setAutoCommit(autocommit)
    } catch {
      case e: Throwable =>
        connection.close()
        throw e
    }
    connection
  }

  def withConnection[A](block: Connection => A): A = {
    withConnection(autocommit = true)(block)
  }

  def withConnection[A](autocommit: Boolean)(block: Connection => A): A = {
    val connection = getConnection(autocommit)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }

  def withTransaction[A](block: Connection => A): A = {
    withConnection(autocommit = false) { connection =>
      try {
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: ControlThrowable =>
          connection.commit()
          throw e
        case e: Throwable =>
          connection.rollback()
          throw e
      }
    }
  }

  def withTransaction[A](isolationLevel: TransactionIsolationLevel)(block: Connection => A): A = {
    withConnection(autocommit = false) { connection =>
      val oldIsolationLevel = connection.getTransactionIsolation
      try {
        connection.setTransactionIsolation(isolationLevel.id)
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: ControlThrowable =>
          connection.commit()
          throw e
        case e: Throwable =>
          connection.rollback()
          throw e
      } finally {
        connection.setTransactionIsolation(oldIsolationLevel)
      }
    }
  }

  // shutdown

  def shutdown(): Unit = {
    closeDataSource(dataSource)
  }

  def databases(): Seq[Database] = {
    Seq(this)
  }

  /**
   * The configuration name for this database.
   */
  override def name: String = ""
}
