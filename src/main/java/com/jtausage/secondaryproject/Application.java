package com.jtausage.secondaryproject;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	private final XADataSourceWrapper wrapper;

	public Application(XADataSourceWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Bean
	@ConfigurationProperties(prefix = "a")
	DataSource a() throws Exception {
		return this.wrapper.wrapDataSource(dataSource("a"));
	}

	@Bean
	@ConfigurationProperties(prefix = "b")
	DataSource b() throws Exception {
		return this.wrapper.wrapDataSource(dataSource("b"));
	}

	@Bean
	DataSourceInitializer aInit(DataSource a) {
		return init(a, "a");
	}

	@Bean
	DataSourceInitializer bInit(DataSource b) {
		return init(b, "b");
	}

	private DataSourceInitializer init(DataSource ds, String name) {
		DataSourceInitializer dsi = new DataSourceInitializer();
		dsi.setDataSource(ds);
		dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(name + ".sql")));
		return dsi;
	}

	private JdbcDataSource dataSource(String type) {
		JdbcDataSource jdbcDataSource = new JdbcDataSource();
		jdbcDataSource.setURL("jdbc:h2:./" + type);
		jdbcDataSource.setUser("sa");
		jdbcDataSource.setPassword("");
		return jdbcDataSource;
	}

}