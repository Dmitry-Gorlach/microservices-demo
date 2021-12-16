package com.microservices.demo.kafka.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration //Indicates that the class has @Bean methods to create Spring beans
public class WebClientConfig {

    @Bean //Indicates that a method produces a bean to be managed by Spring container
    WebClient webClient() {
        return WebClient.builder().build();
    }
}