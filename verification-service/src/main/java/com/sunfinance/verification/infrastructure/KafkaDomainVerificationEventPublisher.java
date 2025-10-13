package com.sunfinance.verification.infrastructure;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunfinance.verification.domain.events.DomainEventPublisher;
import com.sunfinance.verification.service.VerificationService;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDomainVerificationEventPublisher implements DomainEventPublisher {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaDomainVerificationEventPublisher.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public KafkaDomainVerificationEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
			ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public void publish(Object event) {
		
		try {
			String topic = "verification.created";
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send(topic, payload);
			log.info("Published event to topic " + topic + ": " + payload);
		} catch (Exception e) {
			throw new RuntimeException("Failed to publish event to Kafka", e);
		}
	}
}
