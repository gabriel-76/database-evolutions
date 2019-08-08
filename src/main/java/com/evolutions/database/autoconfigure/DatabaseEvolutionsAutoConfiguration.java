package com.evolutions.database.autoconfigure;

import com.evolutions.database.play.evolutions.ApplicationEvolutions;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Enable Auto-configuration for Database Evolutions
 * @author Gabriel Oliveira
 */
@Configuration
@ConditionalOnClass(DataSource.class)
//@ConditionalOnProperty(prefix = "play.evolutions", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(DatabaseEvolutionsProperties.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class })
public class DatabaseEvolutionsAutoConfiguration {

    private final ApplicationEvolutions applicationEvolutions;

    public DatabaseEvolutionsAutoConfiguration(ApplicationEvolutions applicationEvolutions) {
        this.applicationEvolutions = applicationEvolutions;
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public void evoluttionsApply() {
        applicationEvolutions.start();
    }
}
