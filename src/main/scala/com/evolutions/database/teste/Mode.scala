package com.evolutions.database.teste

/**
 * Application mode, either `Dev`, `Test`, or `Prod`.
 *
 * @see [[com.evolutions.database.Mode]]
 */
sealed abstract class Mode(val asJava: com.evolutions.database.Mode)

object Mode {
  case object Dev  extends Mode(com.evolutions.database.Mode.DEV)
  case object Test extends Mode(com.evolutions.database.Mode.TEST)
  case object Prod extends Mode(com.evolutions.database.Mode.PROD)

  lazy val values: Set[com.evolutions.database.teste.Mode] = Set(Dev, Test, Prod)
}

