package com.evolutions.database.autoconfigure

import com.evolutions.database.play.evolutions.ApplicationEvolutions
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnClass, ConditionalOnProperty}
import org.springframework.boot.autoconfigure.jdbc.{DataSourceAutoConfiguration, JdbcTemplateAutoConfiguration}
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}

/**
 * Enable Auto-configuration for Database Evolutions
 *
 * @author Gabriel Oliveira
 */
@Configuration
@ConditionalOnClass(Array(classOf[DataSource]))
@ConditionalOnProperty(prefix = "play.evolutions", name = Array("enabled"), havingValue = "true")
@EnableConfigurationProperties(Array(classOf[DatabaseEvolutionsProperties]))
@AutoConfigureAfter(Array(classOf[DataSourceAutoConfiguration], classOf[JdbcTemplateAutoConfiguration],
  classOf[HibernateJpaAutoConfiguration]))
class DatabaseEvolutionsAutoConfiguration(@Autowired private val applicationEvolutions: ApplicationEvolutions) {

  @Bean
  @ConditionalOnBean(Array(classOf[DataSource]))
  def evolutionsApply(): Unit = {
    applicationEvolutions.start()
  }

}
