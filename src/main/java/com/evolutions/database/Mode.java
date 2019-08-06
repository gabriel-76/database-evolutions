/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.evolutions.database;

/** Application mode, either `DEV`, `TEST`, or `PROD`. */
public enum Mode {
  DEV,
  TEST,
  PROD;

  public com.evolutions.database.teste.Mode asScala() {
    if (this == DEV) {
      return com.evolutions.database.teste.Mode.Dev$.MODULE$;
    } else if (this == PROD) {
      return com.evolutions.database.teste.Mode.Prod$.MODULE$;
    }
    return com.evolutions.database.teste.Mode.Test$.MODULE$;
  }
}
