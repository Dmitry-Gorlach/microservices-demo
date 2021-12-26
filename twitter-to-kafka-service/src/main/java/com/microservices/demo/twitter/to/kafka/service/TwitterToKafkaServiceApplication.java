package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
// It allows you to find Spring beans in other modules, all modules packages start from "com.microservices.demo.{custom}"
@ComponentScan(basePackages ="com.microservices.demo")
// We can use also @RequiredArgsConstructor,it can be used instead of writing the constructor explicitly.
// It will create the constructor at runtime and will be less boilerplate code
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
    private final StreamRunner streamRunner;

    private final StreamInitializer streamInitializer;

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
    public TwitterToKafkaServiceApplication(StreamRunner runner, StreamInitializer initializer) {
        this.streamRunner = runner;
        this.streamInitializer = initializer;
        // I've renamed the auto-generated parameter names to not shadow the class variable. Shadowing makes the code
        // difficult to read, and if you somehow forget or omit to use "this" keyword on the left, it could cause a bug.
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        LOG.info("Application starts...");
        streamInitializer.init();
        streamRunner.start();
    }
}
