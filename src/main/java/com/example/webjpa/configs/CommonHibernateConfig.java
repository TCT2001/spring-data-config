package com.example.webjpa.configs;

import com.example.webjpa.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Properties;

@PropertySource("classpath:persistence.properties")
@RequiredArgsConstructor
@Configuration
public class CommonHibernateConfig {
    private final Environment env;

    @Bean(name = "hibernateProperties")
    public Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.dialect"));
        hibernateProperties.setProperty("hibernate.show_sql", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.show_sql"));
        hibernateProperties.setProperty("hibernate.format_sql", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.format_sql"));
        hibernateProperties.setProperty("hibernate.highlight_sql", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.highlight_sql"));
        hibernateProperties.setProperty("hibernate.use_sql_comments", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.use_sql_comments"));
        hibernateProperties.setProperty("hibernate.generate_statistics", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.generate_statistics"));
        hibernateProperties.setProperty("hibernate.connection.autocommit", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.connection.autocommit"));
        hibernateProperties.setProperty("hibernate.max_fetch_depth", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.max_fetch_depth"));
        hibernateProperties.setProperty("hibernate.jdbc.fetch_size", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.jdbc.fetch_size"));
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.jdbc.batch_size"));

        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.cache.use_second_level_cache"));
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.cache.use_query_cache"));
        hibernateProperties.setProperty("hibernate.cache.region.factory_class", CommonUtils.getNonNullPropertyOrThrow(env, "hibernate.cache.region.factory_class"));
        return hibernateProperties;
    }
}
