package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Objects;

@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final StreamRunner streamRunner;

    // Spring doesn't use reflection with constructor injection approach, reflection makes app run lower.
    // To avoid NoUniqueBeanDefinitionException we can use:
    // 1) type in constructor the same component name for StreamRunner as TwitterToKafkaStreamRunner or
    // 2) should use Qualifier("twitterKafkaStreamRunner")

    // 1) using the same component name
//    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData,
//                                            StreamRunner twitterKafkaStreamRunner) {
//        this.twitterToKafkaServiceConfigData = configData;
//        this.streamRunner = twitterKafkaStreamRunner;
//    }

    // 2) using @Qualifier("componentName")
    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData,
                                           @Qualifier("twitterKafkaStreamRunner") StreamRunner runner) {
        this.twitterToKafkaServiceConfigData = configData;
        this.streamRunner = runner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        LOG.info("Application starts...");
        if(Objects.nonNull(twitterToKafkaServiceConfigData)
                && twitterToKafkaServiceConfigData.getTwitterKeywords() != null) {
            LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[]{})));
            LOG.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
        }
        streamRunner.start();
    }
}
