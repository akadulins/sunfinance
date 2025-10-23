package com.sunfinance.verification.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunfinance.common.model.OutboxEvent;
import com.sunfinance.verification.config.VerificationConfig;
import com.sunfinance.verification.repository.OutboxEventRepository;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

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
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepo.findTop50ByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getEventType(), event.getPayload());
                event.setProcessed(true);
                outboxRepo.save(event);
                log.info("Outbox event succesfully published: " +  event.getPayload());
            } catch (Exception e) {
                log.error("Failed to publish outbox event: " + e.getMessage());
            }
        }
    }
}
