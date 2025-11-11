package com.sunfinance.verification.outbox;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunfinance.common.model.OutboxEvent;
import com.sunfinance.verification.domain.repository.OutboxEventRepository;

@Component
public class OutboxPublisher {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OutboxPublisher.class);

	private final OutboxEventRepository outboxRepo;
	private final KafkaTemplate<String, String> kafkaTemplate;

	public OutboxPublisher(OutboxEventRepository outboxRepo, KafkaTemplate<String, String> kafkaTemplate) {
		this.outboxRepo = outboxRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Scheduled(fixedDelay = 5000)
	@Transactional
	public void publishPendingEvents() {
		List<OutboxEvent> events = outboxRepo.findTop50Unprocessed();

		for (OutboxEvent event : events) {

			try {
				if (event.getTopic() == null || event.getTopic().isBlank()) {
					throw new IllegalArgumentException("Topic cannot be null or empty for event " + event.getId());
				}

				kafkaTemplate.send(event.getTopic(), event.getPayload());
				outboxRepo.markProcessed( event.getId());
				outboxRepo.save(event);
			} catch (Exception e) {
				log.error("Failed to publish event {}: {}", event.getId(), e.getMessage());
			}
		}
	}
}
