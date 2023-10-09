package com.example.demo;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootConfiguration
public class Configuration {

    private static String DB_DRIVER = "org.postgresql.Driver";
    private static String DB_HOST = "wapi.local";
    private static String DB_PORT = "5432";
    private static String DB_NAME = "markets";
    private static String DB_SCHEMA = "musician";
    private static String DB_USER = "musician";
    private static String DB_PASSWORD = "musician_pwd";

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setPackagesToScan("com.example.demo");
        sessionFactory.setHibernateProperties(hibernateConfiguration());
        sessionFactory.setDataSource(dataSource());

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String DB_CONNECTION_URL = new StringBuilder()
                .append("jdbc:postgresql://")
                .append(DB_HOST).append(":")
                .append(DB_PORT).append("/")
                .append(DB_NAME)
                .toString();

        dataSource.setDriverClassName(DB_DRIVER);
        dataSource.setUrl(DB_CONNECTION_URL);
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setSchema(DB_SCHEMA);

        return dataSource;
    }

    private final Properties hibernateConfiguration() {
        Properties dbConfig = new Properties();
        dbConfig.setProperty("hibernate.hbm2ddl.auto", "update");
        dbConfig.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        // dbConfig.setProperty("hibernate.jdbc.time_zone", "UTC");

        // Set 'false' value for property bellow on production server!
        dbConfig.setProperty("hibernate.show_sql", "false");
        dbConfig.setProperty("hibernate.format_sql", "false");

        return dbConfig;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
