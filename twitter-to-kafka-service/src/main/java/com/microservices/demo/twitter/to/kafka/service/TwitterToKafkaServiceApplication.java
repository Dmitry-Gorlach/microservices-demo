package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Objects;

@SpringBootApplication
// It allows you to find Spring beans in other modules, all modules packages start from "com.microservices.demo.{custom}"
@ComponentScan("com.microservices.demo")
// We can use also @RequiredArgsConstructor,it can be used instead of writing the constructor explicitly.
// It will create the constructor at runtime and will be less boilerplate code
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final StreamRunner streamRunner;

    // Spring doesn't use reflection with constructor injection approach, reflection makes the app run lower.
    // To avoid NoUniqueBeanDefinitionException we can use:
    // 1) type in constructor the same component name for StreamRunner as twitterToKafkaStreamRunner or
    // 2) or should use Qualifier("twitterKafkaStreamRunner") but when you add ConditionalOnProperty annotation
    // on both MockKafkaStreamRunner and TwitterKafkaStreamRunner spring will only load one of the beans at runtime
    // so you don't need the Qualifier.If IntelliJ warns you about this, it could just be a small bug in Intellij
    // but it shouldn't effect compiling or running the application.

    // 1) an example using the same component name
//    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData,
//                                            StreamRunner twitterKafkaStreamRunner) {
//        this.twitterToKafkaServiceConfigData = configData;
//        this.streamRunner = twitterKafkaStreamRunner;
//    }

    // 2) using @Qualifier("componentName")
    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData,
                                            StreamRunner runner) {
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
