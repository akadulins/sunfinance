package com.sunfinance.verification.infrastructure.repository;

import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.SubjectType;
import com.sunfinance.common.model.Verification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Verification> query;

    private VerificationRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new VerificationRepositoryImpl();
        // Inject mocked EntityManager
        try {
            var field = VerificationRepositoryImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repository, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should save new verification (persist)")
    void shouldSaveNewVerification() {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        Verification verification = new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));

        // When
        repository.save(verification);

        // Then
        verify(entityManager).persist(verification);
    }

    @Test
    @DisplayName("Should save existing verification (merge)")
    void shouldSaveExistingVerification() {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        Verification verification = new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));
        
        // Set ID to simulate existing entity
        try {
            var idField = Verification.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(verification, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // When
        repository.save(verification);

        // Then
        verify(entityManager).merge(verification);
        verify(entityManager, never()).persist(verification);
    }

    @Test
    @DisplayName("Should find verification by ID")
    void shouldFindVerificationById() {
        // Given
        UUID id = UUID.randomUUID();
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        Verification verification = new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));

        when(entityManager.find(Verification.class, id)).thenReturn(verification);

        // When
        Optional<Verification> result = repository.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(verification, result.get());
    }

    @Test
    @DisplayName("Should return empty when verification not found by ID")
    void shouldReturnEmptyWhenNotFoundById() {
        // Given
        UUID id = UUID.randomUUID();
        when(entityManager.find(Verification.class, id)).thenReturn(null);

        // When
        Optional<Verification> result = repository.findById(id);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find pending verification by subject")
    void shouldFindPendingVerificationBySubject() {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        Verification verification = new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));

        when(entityManager.createQuery(anyString(), eq(Verification.class))).thenReturn(query);
        when(query.setParameter("identity", subject.getIdentity())).thenReturn(query);
        when(query.setParameter("type", subject.getType())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(verification));

        // When
        Optional<Verification> result = repository.findPendingBySubject(subject);

        // Then
        assertTrue(result.isPresent());
        assertEquals(verification, result.get());
    }

    @Test
    @DisplayName("Should return empty when no pending verification found")
    void shouldReturnEmptyWhenNoPendingVerificationFound() {
        // Given
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);

        when(entityManager.createQuery(anyString(), eq(Verification.class))).thenReturn(query);
        when(query.setParameter("identity", subject.getIdentity())).thenReturn(query);
        when(query.setParameter("type", subject.getType())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        // When
        Optional<Verification> result = repository.findPendingBySubject(subject);

        // Then
        assertFalse(result.isPresent());
    }
}