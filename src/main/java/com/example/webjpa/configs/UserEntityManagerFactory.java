package com.example.webjpa.configs;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class UserEntityManagerFactory extends LocalContainerEntityManagerFactoryBean {
    public UserEntityManagerFactory() {
        super();
    }
}
