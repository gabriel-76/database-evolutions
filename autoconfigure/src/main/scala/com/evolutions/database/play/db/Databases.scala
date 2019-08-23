/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database.play.db

import java.sql.Connection

import com.evolutions.database.autoconfigure.DatabaseConf
import javax.sql.DataSource
import org.springframework.stereotype.Service

import scala.util.control.ControlThrowable

@Service
class MyDatabase(ds: DataSource, conf: DatabaseConf) extends Database {

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
  override def name: String = ds.getConnection.getCatalog

  def getConf(): DatabaseConf = {
    conf
  }
}
