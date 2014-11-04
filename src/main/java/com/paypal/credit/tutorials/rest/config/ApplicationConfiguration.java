package com.paypal.credit.tutorials.rest.config;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.paypal.credit.tutorials.rest.resources.AccountResource;

/**
 * This class defines a ApplicationConfiguration.
 */
@Configuration
@ComponentScan("com.paypal.credit.tutorials.rest")
public class ApplicationConfiguration {

    @Bean(destroyMethod = "destroy")
    public Server server(AccountResource accountResource) {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(cxf());
        factory.setServiceBeanObjects(accountResource);
        factory.setProvider(jsonProvider());
        return factory.create();
    }
    
    @Bean
    public SpringBus cxf() {
        return new SpringBus();
    }
    
    @Bean
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider();
    }
    
}
