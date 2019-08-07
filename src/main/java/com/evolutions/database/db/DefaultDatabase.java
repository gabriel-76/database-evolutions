/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database.db;

import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import javax.sql.DataSource;
import java.sql.Connection;

/** Default delegating implementation of the database API. */
public class DefaultDatabase implements Database {

  private final com.evolutions.database.play.db.Database db;

  public DefaultDatabase( com.evolutions.database.play.db.Database database) {
    this.db = database;
  }

  @Override
  public String getName() {
    return db.name();
  }

  @Override
  public DataSource getDataSource() {
    return db.dataSource();
  }

  @Override
  public String getUrl() {
    return db.url();
  }

  @Override
  public Connection getConnection() {
    return db.getConnection();
  }

  @Override
  public Connection getConnection(boolean autocommit) {
    return db.getConnection(autocommit);
  }

  @Override
  public void withConnection(ConnectionRunnable block) {
    db.withConnection(connectionFunction(block));
  }

  @Override
  public <A> A withConnection(ConnectionCallable<A> block) {
    return db.withConnection(connectionFunction(block));
  }

  @Override
  public void withConnection(boolean autocommit, ConnectionRunnable block) {
    db.withConnection(autocommit, connectionFunction(block));
  }

  @Override
  public <A> A withConnection(boolean autocommit, ConnectionCallable<A> block) {
    return db.withConnection(autocommit, connectionFunction(block));
  }

  @Override
  public void withTransaction(ConnectionRunnable block) {
    db.withTransaction(connectionFunction(block));
  }

  @Override
  public void withTransaction(TransactionIsolationLevel isolationLevel, ConnectionRunnable block) {
    db.withTransaction(isolationLevel.asScala(), connectionFunction(block));
  }

  @Override
  public <A> A withTransaction(ConnectionCallable<A> block) {
    return db.withTransaction(connectionFunction(block));
  }

  @Override
  public <A> A withTransaction(
      TransactionIsolationLevel isolationLevel, ConnectionCallable<A> block) {
    return db.withTransaction(isolationLevel.asScala(), connectionFunction(block));
  }

  @Override
  public void shutdown() {
    db.shutdown();
  }

  /**
   * Create a Scala function wrapper for ConnectionRunnable.
   *
   * @param block a Java functional interface instance to wrap
   * @return a scala function that wraps the given block
   */
  AbstractFunction1<Connection, BoxedUnit> connectionFunction(final ConnectionRunnable block) {
    return new AbstractFunction1<Connection, BoxedUnit>() {
      public BoxedUnit apply(Connection connection) {
        try {
          block.run(connection);
          return BoxedUnit.UNIT;
        } catch (java.sql.SQLException e) {
          throw new RuntimeException("Connection runnable failed", e);
        }
      }
    };
  }

  /**
   * Create a Scala function wrapper for ConnectionCallable.
   *
   * @param block a Java functional interface instance to wrap
   * @param <A> the provided block's return type
   * @return a scala function wrapping the given block
   */
  <A> AbstractFunction1<Connection, A> connectionFunction(final ConnectionCallable<A> block) {
    return new AbstractFunction1<Connection, A>() {
      public A apply(Connection connection) {
        try {
          return block.call(connection);
        } catch (java.sql.SQLException e) {
          throw new RuntimeException("Connection callable failed", e);
        }
      }
    };
  }
}
