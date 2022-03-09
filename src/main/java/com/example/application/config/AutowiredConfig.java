package com.example.application.config;


import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Configuration

public class AutowiredConfig {

    @Bean
    public static EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("com.example.application.eCareApplication");
    }

    @Bean
    public static EntityManager getEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }
    
    @Bean
    public DozerBeanMapper beanMapper() {
        return new DozerBeanMapper();
    }
}
