package com.evolutions.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootApplication(scanBasePackages = {"com.evolutions.database.*"})
public class DatabaseEvolutionsApplication {

//	@Autowired
//	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(DatabaseEvolutionsApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//
//			System.out.println(jdbcTemplate.queryForList("SHOW TABLES"));
//
////			System.out.println(jdbcTemplate.queryForList("select * from information_schema.tables"));
//
////			System.out.println(jdbcTemplate.queryForList("SELECT * FROM PLAY_EVOLUTIONS"));
//
//		};
//	}

}
