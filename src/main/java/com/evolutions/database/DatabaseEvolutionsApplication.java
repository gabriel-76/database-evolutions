package com.evolutions.database;

import com.evolutions.database.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@SpringBootApplication
public class DatabaseEvolutionsApplication {

	@Autowired
	private DbConfig config;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EvolutionsReader evolutionsReader;

	@Autowired
    private ResourceEvolutionsReader resourceEvolutionsReader;

	@Autowired
    private DefaultEvolutionsApi defaultEvolutionsApi;

	@Autowired
	private Reader reader;

	@Autowired
	private ApplicationEvolutions applicationEvolutions;

	public static void main(String[] args) {
		SpringApplication.run(DatabaseEvolutionsApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

//			var scripts = defaultEvolutionsApi.scripts("testedb", resourceEvolutionsReader ,"");

//			var e = resourceEvolutionsReader.evolutions("");

//			defaultEvolutionsApi.evolve("testedb", scripts,true,"");

			applicationEvolutions.start();

			System.out.println(jdbcTemplate.queryForList("SHOW TABLES"));

			System.out.println(jdbcTemplate.queryForList("SELECT * FROM PLAY_EVOLUTIONS"));

		};
	}

}
