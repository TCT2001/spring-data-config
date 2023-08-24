package com.example.webjpa.controllers;

import com.example.webjpa.CommonUtils;
import com.example.webjpa.configs.ProductionEntityManagerFactory;
import com.example.webjpa.configs.UserEntityManagerFactory;
import com.example.webjpa.entities.db1.User;
import com.example.webjpa.repositories.db1.UserRepository;
import com.example.webjpa.repositories.db2.ProductionRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    private final UserRepository userRepository;
    private final ProductionRepository productionRepository;
    private final UserEntityManagerFactory emfUser;
    private final ProductionEntityManagerFactory emfProduction;

    public TestController(UserRepository userRepository,
                          ProductionRepository productionRepository,
                          UserEntityManagerFactory emf,
                          ProductionEntityManagerFactory emfProduction) {
        this.userRepository = userRepository;
        this.productionRepository = productionRepository;
        this.emfUser = emf;
        this.emfProduction = emfProduction;
    }

    @GetMapping("/utils")
    public String testBeanUtils() {
        User user = new User(1L, "");
        User user2 = new User();
        CommonUtils.copyProperties(user, user2);
        log.info(user2.toString());
        return "1";
    }

    @GetMapping("/sess")
    public String testSession() {
        try (EntityManager userEntityManager = emfUser.getObject().createEntityManager()) {
            Session session = userEntityManager.unwrap(Session.class);
            session.beginTransaction().commit();
            session.beginTransaction().commit();
            session.beginTransaction().commit();
            session.beginTransaction().commit();
        }
        return "1";
    }

    @GetMapping("/auto")
    public String testAutoUpdate() {
        try (EntityManager userEntityManager = emfUser.getObject().createEntityManager()) {
            userEntityManager.getTransaction().begin();
            User user1 = new User("TCT_01");
            userEntityManager.persist(user1);
            userEntityManager.getTransaction().commit();
            log.info(user1.toString());

            userEntityManager.getTransaction().begin();
            Optional userInSession1 = userEntityManager
                    .createNamedQuery("Person.findById")
                    .setParameter("id", 1)
                    .getResultStream()
                    .findFirst();

            User userInSession2 = (User) userInSession1.orElse(null);
            log.info(userInSession2.toString());

            userInSession2.setName("TCC_01_NEW");
            // No Need To Merge: Dirty Check
            userEntityManager.getTransaction().commit();
            //userEntityManager.getTransaction().commit();
            //userEntityManager.getTransaction().commit();

            userEntityManager.getTransaction().begin();
            Optional userInSession3 = userEntityManager
                    .createNamedQuery("Person.findById")
                    .setParameter("id", 1)
                    .getResultStream()
                    .findFirst();

            User userInSession4 = (User) userInSession3.orElse(null);
            log.info(userInSession4.toString());
        }
        return "1";
    }

    @GetMapping("/test")
    public String testGeneratorType() {
        try (EntityManager userEntityManager = emfUser.getObject().createEntityManager()) {
            userEntityManager.getTransaction().begin();
            User user1 = new User("TCT_01");
            User user2 = new User("TCT_02");
            userEntityManager.persist(user1);
            userEntityManager.persist(user2);
            userEntityManager.getTransaction().commit();
            log.info(user1.toString());
            log.info(user2.toString());
        }

        return "1";
    }

    @GetMapping
    public String test() {
        try (EntityManager entityManager = emfUser.getObject().createEntityManager()) {
            entityManager.getTransaction().begin();

            User user = new User();
            user.setId(1L);
            user.setName("Hello");

            entityManager.persist(user);
            user.setName("Hi");
            entityManager.merge(user);

            User userInSession = (User) entityManager
                    .createNamedQuery("Person.findById")
                    .setParameter("id", 1)
                    .getSingleResult();

            System.out.println(userInSession);

            entityManager.getTransaction().rollback();

            Optional userInSession1 = entityManager
                    .createNamedQuery("Person.findById")
                    .setParameter("id", 1)
                    .getResultStream()
                    .findFirst();

            User userInSession2 = (User) userInSession1.orElse(null);

            System.out.println(userInSession2);

            Optional<User> userOptional2 = userRepository.findById(1L);
            log.info(String.valueOf(userOptional2.isEmpty()));

            // entityManager.getTransaction().commit();
            return "test";
        }
    }
}
