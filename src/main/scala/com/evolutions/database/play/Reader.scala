package com.evolutions.database.play

import java.io.IOException
import java.lang.Integer.valueOf
import java.util.Objects

import com.evolutions.database.config.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.core.io.{Resource, ResourceLoader}
import org.springframework.stereotype.Service

@Service
class Reader(@Autowired private val resourceLoader: ResourceLoader, @Autowired private val config: Config) {

  @throws[IOException]
  private def loadResources(pattern: String) =
    ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern)

  @throws[IOException]
  private[database] def loadEvolutions(database: String = "") =
    loadResources(String.format("classpath*:%1$s/*.sql", config.getPath))
      .sorted((o1: Resource, o2: Resource) => valueOf(name(o1))
      .compareTo(name(o2).toInt))

  private def name(resource: Resource) = Objects.requireNonNull(resource.getFilename).split("\\.sql")(0)
}
