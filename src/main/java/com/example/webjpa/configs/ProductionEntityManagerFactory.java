package com.example.webjpa.configs;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class ProductionEntityManagerFactory extends LocalContainerEntityManagerFactoryBean {
    public ProductionEntityManagerFactory() {
        super();
    }
}
