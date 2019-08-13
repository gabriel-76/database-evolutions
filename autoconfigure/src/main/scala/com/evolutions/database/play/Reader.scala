package com.evolutions.database.play

import java.io.{IOException}
import java.lang.Integer.valueOf
import java.util.Objects

import com.evolutions.database.autoconfigure.DatabaseEvolutionsProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.core.io.{Resource, ResourceLoader}
import org.springframework.stereotype.Service

@Service
class Reader(@Autowired private val resourceLoader: ResourceLoader, @Autowired private val config: DatabaseEvolutionsProperties) {

  @throws[IOException]
  private def loadResources(pattern: String) =
    ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern)

  @throws[IOException]
  private[database] def loadEvolutions(database: String = "") =
    loadResources(String.format("classpath*:%1$s/*.sql", config.getPath))
      .sorted((o1: Resource, o2: Resource) => valueOf(name(o1))
      .compareTo(name(o2).toInt))

  private def name(resource: Resource) = Objects.requireNonNull(resource.getFilename).split("\\.sql")(0)

  @throws[IOException]
  def load(revision: Integer) = {
    val array = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
      .getResources(String.format("classpath*:%1$s/%2$s.sql", config.getPath, revision.toString))
    if (array.nonEmpty) array(0).getInputStream
    else null
  }
}
