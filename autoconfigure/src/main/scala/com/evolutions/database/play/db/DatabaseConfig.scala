package com.evolutions.database.play.db

import com.evolutions.database.play.Environments

/**
 * The generic database configuration.
 *
 * @param driver The driver
 * @param url The jdbc URL
 * @param username The username
 * @param password The password
 * @param jndiName The JNDI name
 */

case class DatabaseConfig(
  driver: Option[String],
  url: Option[String],
  username: Option[String],
  password: Option[String],
  jndiName: Option[String]
)

object DatabaseConfig {

//  def fromConfig(config: Configuration, environments: Environments) = {
  def fromConfig(environments: Environments) = {

//    val driver          = config.get[Option[String]]("driver")
//    val (url, userPass) = ConnectionPool.extractUrl(config.get[Option[String]]("url"), environment.mode)
//    val username        = config.getDeprecated[Option[String]]("username", "user").orElse(userPass.map(_._1))
//    val password        = config.getDeprecated[Option[String]]("password", "pass").orElse(userPass.map(_._2))
//    val jndiName        = config.get[Option[String]]("jndiName")

    val driver          = Option("")
    val url             = Option("")
    val username        = Option("")
    val password        = Option("")
    val jndiName        = Option("")

    DatabaseConfig(driver, url, username, password, jndiName)
  }
}
