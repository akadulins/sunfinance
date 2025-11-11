package com.sunfinance.verification.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunfinance.common.events.VerificationConfirmed;
import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.SubjectType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaDomainVerificationEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private KafkaDomainVerificationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new KafkaDomainVerificationEventPublisher(kafkaTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should publish event to Kafka successfully")
    void shouldPublishEventSuccessfully() throws JsonProcessingException {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        VerificationConfirmed event = new VerificationConfirmed(UUID.randomUUID(), "123456", subject);
        String expectedJson = "{\"id\":\"...\"}";
        
        when(objectMapper.writeValueAsString(event)).thenReturn(expectedJson);
        when(kafkaTemplate.send(anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        publisher.publish(event);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(kafkaTemplate).send(topicCaptor.capture(), payloadCaptor.capture());
        
        assertEquals("verification.created", topicCaptor.getValue());
        assertEquals(expectedJson, payloadCaptor.getValue());
    }

    @Test
    @DisplayName("Should throw RuntimeException when serialization fails")
    void shouldThrowExceptionWhenSerializationFails() throws JsonProcessingException {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        VerificationConfirmed event = new VerificationConfirmed(UUID.randomUUID(), "123456", subject);
        
        when(objectMapper.writeValueAsString(event))
            .thenThrow(new JsonProcessingException("Serialization error") {});

        // When & Then
        assertThrows(RuntimeException.class, () -> publisher.publish(event));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw RuntimeException when Kafka send fails")
    void shouldThrowExceptionWhenKafkaSendFails() throws JsonProcessingException {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        VerificationConfirmed event = new VerificationConfirmed(UUID.randomUUID(), "123456", subject);
        
        when(objectMapper.writeValueAsString(event)).thenReturn("{}");
        when(kafkaTemplate.send(anyString(), anyString()))
            .thenThrow(new RuntimeException("Kafka error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }
}