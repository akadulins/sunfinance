package com.sunfinance.verification.service;

import com.sunfinance.common.exceptions.*;
import com.sunfinance.common.model.*;
import com.sunfinance.verification.application.*;
import com.sunfinance.verification.config.VerificationConfig;
import com.sunfinance.verification.domain.events.DomainEventPublisher;
import com.sunfinance.verification.domain.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock private VerificationRepository verificationRepository;
    @Mock private OutboxEventRepository outboxRepository;
    @Mock private DomainEventPublisher eventPublisher;

    private VerificationService service;
    private VerificationConfig config;

    @BeforeEach
    void setUp() {
        config = new VerificationConfig();
        config.setCodeLength(6);
        config.setValidityPeriod(Duration.ofMinutes(5));
        service = new VerificationService(verificationRepository, config, outboxRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should create verification")
    void shouldCreateVerification() {
        CreateVerificationCommand cmd = new CreateVerificationCommand(
            "test@example.com", "email_confirmation", "user-info");
        when(verificationRepository.findPendingBySubject(any())).thenReturn(Optional.empty());

        UUID id = service.createVerification(cmd);

        //assertNotNull(id);
        verify(verificationRepository).save(any(Verification.class));
        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("Should throw DuplicateVerificationException")
    void shouldThrowDuplicateException() {
        CreateVerificationCommand cmd = new CreateVerificationCommand(
            "test@example.com", "email_confirmation", "user-info");
        Verification existing = new Verification(
            new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION),
            "user-info", "123456", Duration.ofMinutes(5));
        when(verificationRepository.findPendingBySubject(any())).thenReturn(Optional.of(existing));

        assertThrows(DuplicateVerificationException.class, () -> service.createVerification(cmd));
    }

    @Test
    @DisplayName("Should generate 6-digit code")
    void shouldGenerate6DigitCode() {
        CreateVerificationCommand cmd = new CreateVerificationCommand(
            "test@example.com", "email_confirmation", "user-info");
        when(verificationRepository.findPendingBySubject(any())).thenReturn(Optional.empty());

        service.createVerification(cmd);

        ArgumentCaptor<Verification> captor = ArgumentCaptor.forClass(Verification.class);
        verify(verificationRepository).save(captor.capture());
        assertEquals(6, captor.getValue().getCode().length());
    }

    @Test
    @DisplayName("Should confirm verification")
    void shouldConfirmVerification() throws VerificationExpiredException {
        UUID id = UUID.randomUUID();
        Verification v = new Verification(
            new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION),
            "user-info", "123456", Duration.ofMinutes(5));
        ConfirmVerificationCommand cmd = new ConfirmVerificationCommand(id, "123456", "user-info");
        when(verificationRepository.findById(id)).thenReturn(Optional.of(v));

        service.confirmVerification(cmd);

        assertTrue(v.isConfirmed());
        verify(verificationRepository).save(v);
    }

    @Test
    @DisplayName("Should throw VerificationNotFoundException")
    void shouldThrowNotFoundException() {
        ConfirmVerificationCommand cmd = new ConfirmVerificationCommand(
            UUID.randomUUID(), "123456", "user-info");
        when(verificationRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(VerificationNotFoundException.class, () -> service.confirmVerification(cmd));
    }
}