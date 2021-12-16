package com.microservices.demo.kafka.admin.exception;

/**
 * Exception class for Kafka client error situations.
 */
public class KafkaClientException extends RuntimeException {
    public KafkaClientException() {
        super();
    }

    public KafkaClientException(String message) {
        super(message);
    }

    public KafkaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}