package com.sunfinance.verification.outbox;

import com.sunfinance.common.model.OutboxEvent;
import com.sunfinance.verification.domain.repository.OutboxEventRepository;
import com.sunfinance.verification.outbox.OutboxPublisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock private OutboxEventRepository outboxRepository;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @InjectMocks private OutboxPublisher publisher;

    @Test
    @DisplayName("Should publish pending events")
    void shouldPublishPendingEvents() {
        OutboxEvent event = new OutboxEvent("topic", "payload");
        when(outboxRepository.findTop50Unprocessed()).thenReturn(Arrays.asList(event));
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));

        publisher.publishPendingEvents();

        verify(kafkaTemplate).send("topic", "payload");
        verify(outboxRepository).markProcessed(event.getId());
    }

    @Test
    @DisplayName("Should handle empty outbox")
    void shouldHandleEmptyOutbox() {
        when(outboxRepository.findTop50Unprocessed()).thenReturn(Collections.emptyList());

        publisher.publishPendingEvents();

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}