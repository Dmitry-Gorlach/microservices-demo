package com.microservices.demo.twitter.to.kafka.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {
    //the name is important, so it should by like in application.yml "-" replaced by Capital char
    private List<String> twitterKeywords;
    private String welcomeMessage;
}
