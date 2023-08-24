package com.example.webjpa.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
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
        basePackages = "com.example.webjpa.repositories.db2",
        entityManagerFactoryRef = "productEntityManager",
        transactionManagerRef = "productTransactionManager"
)
@RequiredArgsConstructor
public class PersistenceProductAutoConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource productDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @DependsOn("productDataSource")
    public ProductionEntityManagerFactory productEntityManager(
            @Qualifier(value = "hibernateProperties") Properties properties,
            @Qualifier(value = "productDataSource") DataSource dataSource
    ) {
        final var em = new ProductionEntityManagerFactory();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.webjpa.entities.db2");
        em.setPersistenceUnitName("DB2");

        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(properties);
        return em;
    }

    @Bean
    @DependsOn("productEntityManager")
    public PlatformTransactionManager productTransactionManager(ProductionEntityManagerFactory emf) {
        final var transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf.getObject());
        return transactionManager;
    }
}