package com.evolutions.database.play

import java.io.{File, InputStream}

import com.evolutions.database.autoconfigure.DatabaseEvolutionsProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * The environment for the application.
 *
 * Captures concerns relating to the classloader and the filesystem for the application.
 *
 * @param rootPath The root path that the application is deployed at.
 * @param classLoader The classloader that all application classes and resources can be loaded from.
 * @param mode The mode of the application.
 */
@Service
case class Environments (@Autowired private val config: DatabaseEvolutionsProperties) {

  val rootPath: File = new File("")
  val classLoader: ClassLoader = getClass.getClassLoader
  val mode: Mode = com.evolutions.database.Mode.valueOf(config.getMode.toUpperCase).asScala()

  /**
   * Retrieves a file relative to the application root path.
   *
   * Note that it is up to you to manage the files in the application root path in production.  By default, there will
   * be nothing available in the application root path.
   *
   * For example, to retrieve some deployment specific data file:
   * {{{
   * val myDataFile = application.getFile("data/data.xml")
   * }}}
   *
   * @param relativePath relative path of the file to fetch
   * @return a file instance; it is not guaranteed that the file exists
   */
  def getFile(relativePath: String): File = new File(rootPath, relativePath)

  /**
   * Retrieves a file relative to the application root path.
   * This method returns an Option[File], using None if the file was not found.
   *
   * Note that it is up to you to manage the files in the application root path in production.  By default, there will
   * be nothing available in the application root path.
   *
   * For example, to retrieve some deployment specific data file:
   * {{{
   * val myDataFile = application.getExistingFile("data/data.xml")
   * }}}
   *
   * @param relativePath the relative path of the file to fetch
   * @return an existing file
   */
  def getExistingFile(relativePath: String): Option[File] = Some(getFile(relativePath)).filter(_.exists)

  /**
   * Scans the application classloader to retrieve a resource.
   *
   * The conf directory is included on the classpath, so this may be used to look up resources, relative to the conf
   * directory.
   *
   * For example, to retrieve the conf/logback.xml configuration file:
   * {{{
   * val maybeConf = application.resource("logback.xml")
   * }}}
   *
   * @param name the absolute name of the resource (from the classpath root)
   * @return the resource URL, if found
   */
  def resource(name: String): Option[java.net.URL] = {
    val n = name.stripPrefix("/")
    Option(classLoader.getResource(n))
  }

  /**
   * Scans the application classloader to retrieve a resource’s contents as a stream.
   *
   * The conf directory is included on the classpath, so this may be used to look up resources, relative to the conf
   * directory.
   *
   * For example, to retrieve the conf/logback.xml configuration file:
   * {{{
   * val maybeConf = application.resourceAsStream("logback.xml")
   * }}}
   *
   * @param name the absolute name of the resource (from the classpath root)
   * @return a stream, if found
   */
  def resourceAsStream(name: String): Option[InputStream] = {
    val n = name.stripPrefix("/")
    Option(classLoader.getResourceAsStream(n))
  }

  /**
   * @return Returns the Java version for this environment.
   */
  def asJava: com.evolutions.database.Environments = new com.evolutions.database.Environments(this)

}
