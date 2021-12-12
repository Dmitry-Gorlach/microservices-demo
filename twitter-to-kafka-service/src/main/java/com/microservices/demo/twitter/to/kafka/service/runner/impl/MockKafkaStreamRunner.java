package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = new String[] {
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetur",
            "adipiscing",
            "elit",
            "Mauris",
            "vel",
            "fringilla"
    };

    private static final String tweetAsRawJson = "{\n" +
            "  \"created_at\": \"{0}\",\n" +
            "  \"id\": \"{1}\",\n" +
            "  \"text\": \"{2}\",\n" +
            "  \"user\": {\n" +
            "    \"id\": \"{3}\"\n" +
            "  }\n" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData configData,
                                 TwitterKafkaStatusListener statusListener) {
        this.twitterToKafkaServiceConfigData = configData;
        this.twitterKafkaStatusListener = statusListener;
    }

    @Override
    public void start() throws TwitterException {
        String [] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        final Integer minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
        final Integer maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
        final Long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
        LOG.info("Starting mock filtering twitter streams for keywords {}", Arrays.toString(keywords));
        while(true) {
            String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
            Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
            twitterKafkaStatusListener.onStatus(status); //used only in mock for init iteration infinite loop
            sleep(sleepTimeMs);
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create!");
        }
    }

    private String getFormattedTweet(String[] keywords, Integer minTweetLength, Integer maxTweetLength) {
        String[] params = new String[] {
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        String tweet = tweetAsRawJson;
        for(int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params [i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, Integer minTweetLength, Integer maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        for(int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if(i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }
}
