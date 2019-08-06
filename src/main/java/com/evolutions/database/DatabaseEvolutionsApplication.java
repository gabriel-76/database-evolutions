package com.evolutions.database;

import com.evolutions.database.api.DefaultEvolutionsApi;
import com.evolutions.database.api.GreetingInScala;
import com.evolutions.database.api.Reader;
import com.evolutions.database.api.ResourceEvolutionsReader;
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
	private Config config;

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

	public static void main(String[] args) {
		SpringApplication.run(DatabaseEvolutionsApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println(dataSource.getConnection().getSchema());

			System.out.println(config.getEnable());
			System.out.println(config.getSchema());
			System.out.println(config.getAutocommit());
			System.out.println(config.getAutocouseLocks());
			System.out.println(config.getAutoApply());
			System.out.println(config.getAutoApplyDowns());
			System.out.println(config.getSkipApplyDownsOnly());

			var scripts = defaultEvolutionsApi.scripts("testedb", resourceEvolutionsReader ,"");

//			var e = resourceEvolutionsReader.evolutions("");

			defaultEvolutionsApi.evolve("testedb", scripts,true,"");

			GreetingInScala greetingInScala = new GreetingInScala();
			System.out.println(greetingInScala.greet());

			System.out.println(jdbcTemplate.queryForObject("SELECT 1 = 1", Boolean.class));

			jdbcTemplate.execute("CREATE TABLE t(id INTEGER, NOME VARCHAR)");
			jdbcTemplate.execute("INSERT INTO t (id, nome) VALUES (1, 'hjdfashjdgshd')");

			System.out.println(jdbcTemplate.queryForObject("SELECT nome FROM t WHERE id = 1", String.class));

			System.out.println(jdbcTemplate.queryForList("SHOW TABLES"));

			System.out.println(evolutionsReader.loadEvolutions().count());

		};
	}

}
