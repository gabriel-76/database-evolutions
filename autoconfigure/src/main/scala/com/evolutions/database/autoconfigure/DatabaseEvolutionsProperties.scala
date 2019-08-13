package com.evolutions.database.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for Database Evolutions.
 *
 * @author Gabriel Oliveira
 */
@ConfigurationProperties(prefix = "play.evolutions")
class DatabaseEvolutionsProperties {

  private var enable = true

  private var schema = ""

  private var autocommit = true

  private var useLocks = false

  private var autoApply = false

  private var autoApplyDowns = false

  private var skipApplyDownsOnly = false

  private var mode = "dev"

  private var path = "evolutions"

  def isEnable: Boolean = enable

  def setEnable(enable: Boolean): Unit = {
    this.enable = enable
  }

  def getSchema: String = schema

  def setSchema(schema: String): Unit = {
    this.schema = schema
  }

  def isAutocommit: Boolean = autocommit

  def setAutocommit(autocommit: Boolean): Unit = {
    this.autocommit = autocommit
  }

  def isUseLocks: Boolean = useLocks

  def setUseLocks(useLocks: Boolean): Unit = {
    this.useLocks = useLocks
  }

  def isAutoApply: Boolean = autoApply

  def setAutoApply(autoApply: Boolean): Unit = {
    this.autoApply = autoApply
  }

  def isAutoApplyDowns: Boolean = autoApplyDowns

  def setAutoApplyDowns(autoApplyDowns: Boolean): Unit = {
    this.autoApplyDowns = autoApplyDowns
  }

  def isSkipApplyDownsOnly: Boolean = skipApplyDownsOnly

  def setSkipApplyDownsOnly(skipApplyDownsOnly: Boolean): Unit = {
    this.skipApplyDownsOnly = skipApplyDownsOnly
  }

  def getMode: String = mode

  def setMode(mode: String): Unit = {
    this.mode = mode
  }

  def getPath: String = path

  def setPath(path: String): Unit = {
    this.path = path
  }

}
