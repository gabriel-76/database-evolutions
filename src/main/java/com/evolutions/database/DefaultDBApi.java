/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database;

import com.evolutions.database.api.Scala;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/** Default delegating implementation of the DB API. */
public class DefaultDBApi implements DBApi {

  private com.evolutions.database.api.DBApi dbApi;
  private List<Database> databases;
  private Map<String, Database> databaseByName;

  public DefaultDBApi(com.evolutions.database.api.DBApi dbApi) {
    this.dbApi = dbApi;

    ImmutableList.Builder<Database> databases = new ImmutableList.Builder<Database>();
    ImmutableMap.Builder<String, Database> databaseByName =
        new ImmutableMap.Builder<String, Database>();
    for ( com.evolutions.database.api.Database db : Scala.asJava(dbApi.databases())) {
      Database database = new DefaultDatabase(db);
      databases.add(database);
      databaseByName.put(database.getName(), database);
    }
    this.databases = databases.build();
    this.databaseByName = databaseByName.build();
  }

  public List<Database> getDatabases() {
    return databases;
  }

  public Database getDatabase(String name) {
    return databaseByName.get(name);
  }

  public void shutdown() {
    dbApi.shutdown();
  }
}
