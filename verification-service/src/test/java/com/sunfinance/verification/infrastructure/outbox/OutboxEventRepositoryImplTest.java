package com.sunfinance.verification.infrastructure.outbox;

import com.sunfinance.common.model.OutboxEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<OutboxEvent> query;

    @Mock
    private jakarta.persistence.Query updateQuery;

    private OutboxEventRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new OutboxEventRepositoryImpl();
        // Inject mocked EntityManager via reflection
        try {
            var field = OutboxEventRepositoryImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repository, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should save outbox event")
    void shouldSaveOutboxEvent() {
        // Given
        OutboxEvent event = new OutboxEvent("test-topic", "test-payload");

        // When
        repository.save(event);

        // Then
        verify(entityManager).persist(event);
    }

    @Test
    @DisplayName("Should find top 50 unprocessed events")
    void shouldFindTop50UnprocessedEvents() {
        // Given
        OutboxEvent event1 = new OutboxEvent("topic1", "payload1");
        OutboxEvent event2 = new OutboxEvent("topic2", "payload2");
        List<OutboxEvent> expectedEvents = Arrays.asList(event1, event2);

        when(entityManager.createQuery(anyString(), eq(OutboxEvent.class))).thenReturn(query);
        when(query.setMaxResults(50)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedEvents);

        // When
        List<OutboxEvent> result = repository.findTop50Unprocessed();

        // Then
        assertEquals(2, result.size());
        verify(query).setMaxResults(50);
    }

    @Test
    @DisplayName("Should mark event as processed")
    void shouldMarkEventAsProcessed() {
        // Given
        UUID eventId = UUID.randomUUID();

        when(entityManager.createQuery(anyString())).thenReturn(updateQuery);
        when(updateQuery.setParameter("id", eventId)).thenReturn(updateQuery);
        when(updateQuery.executeUpdate()).thenReturn(1);

        // When
        repository.markProcessed(eventId);

        // Then
        verify(updateQuery).setParameter("id", eventId);
        verify(updateQuery).executeUpdate();
    }
}