/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database;

/** Application mode, either `DEV`, `TEST`, or `PROD`. */
public enum Mode {
  DEV,
  TEST,
  PROD;

  public com.evolutions.database.api.Mode asScala() {
    if (this == DEV) {
      return com.evolutions.database.api.Mode.Dev$.MODULE$;
    } else if (this == PROD) {
      return com.evolutions.database.api.Mode.Prod$.MODULE$;
    }
    return com.evolutions.database.api.Mode.Test$.MODULE$;
  }
}
