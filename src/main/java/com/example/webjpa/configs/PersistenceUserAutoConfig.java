package com.example.webjpa.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource({"classpath:persistence.properties"})
@EnableJpaRepositories(
        basePackages = "com.example.webjpa.repositories.db1",
        entityManagerFactoryRef = "userEntityManager",
        transactionManagerRef = "userTransactionManager"
)
@RequiredArgsConstructor
public class PersistenceUserAutoConfig {
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public UserEntityManagerFactory userEntityManager(
            @Qualifier(value = "hibernateProperties") Properties properties,
            @Qualifier(value = "userDataSource") DataSource dataSource
    ) {
        final var em = new UserEntityManagerFactory();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.webjpa.entities.db1");
        em.setPersistenceUnitName("DB1");

        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(properties);

        return em;
    }

    @Primary
    @Bean
    @DependsOn("userEntityManager")
    public PlatformTransactionManager userTransactionManager(UserEntityManagerFactory emf) {
        final var transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf.getObject());
        return transactionManager;
    }

}