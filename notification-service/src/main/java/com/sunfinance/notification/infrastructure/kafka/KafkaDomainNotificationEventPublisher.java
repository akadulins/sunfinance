package com.sunfinance.notification.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunfinance.common.events.DomainEventPublisher;
import com.sunfinance.notification.consumer.VerificationCreatedConsumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDomainNotificationEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaDomainNotificationEventPublisher.class);

    public KafkaDomainNotificationEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Object event) {

        try {
            String topic = "notification.created";
            String payload = objectMapper.writeValueAsString(event);
            log.info("Publishing event to Kafka topic [{}]: {}", topic, event);
            log.info("Event successfully published to Kafka topic [{}]", topic);
            kafkaTemplate.send(topic, payload).get();
            log.info("Published event to topic " + topic + ": " + payload);
        } catch (Exception e) {
        	log.error("Failed to publish event to Kafka topic [{}]. Cause: {}", "notification.created", e.getMessage(), e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
}
