/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database;

import java.util.List;

/** DB API for managing application databases. */
public interface DBApi {

  /** @return all configured databases. */
  public List<Database> getDatabases();

  /**
   * @param name the configuration name of the database
   * @return Get database with given configuration name.
   */
  public Database getDatabase(String name);

  /** Shutdown all databases, releasing resources. */
  public void shutdown();
}
