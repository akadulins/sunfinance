package com.sunfinance.verification.domain;

import com.sunfinance.common.exceptions.InvalidCodeException;
import com.sunfinance.common.exceptions.VerificationExpiredException;
import com.sunfinance.common.exceptions.VerificationForbiddenException;
import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.SubjectType;
import com.sunfinance.common.model.Verification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTest {

    @Test
    @DisplayName("Should create verification with valid data")
    void shouldCreateVerificationWithValidData() {
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        Verification verification = new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));
        
        //assertNotNull(verification.getId());
        assertEquals("test@example.com", verification.getSubject().getIdentity());
        assertEquals("123456", verification.getCode());
        assertFalse(verification.isConfirmed());
    }

    @Test
    @DisplayName("Should not be expired within TTL")
    void shouldNotBeExpiredWithinTTL() {
        Verification verification = createVerification();
        assertFalse(verification.isExpired());
    }

    @Test
    @DisplayName("Should be expired after TTL")
    void shouldBeExpiredAfterTTL() {
        Verification verification = createVerification();
        verification.setExpiresAt(Instant.now().minus(Duration.ofMinutes(1)));
        assertTrue(verification.isExpired());
    }

    @Test
    @DisplayName("Should confirm with correct code")
    void shouldConfirmWithCorrectCode() throws VerificationExpiredException {
        Verification verification = createVerification();
        verification.confirm("123456", "user-info");
        assertTrue(verification.isConfirmed());
    }

    @Test
    @DisplayName("Should throw InvalidCodeException")
    void shouldThrowInvalidCodeException() {
        Verification verification = createVerification();
        assertThrows(InvalidCodeException.class, 
            () -> verification.confirm("wrong", "user-info"));
    }

    @Test
    @DisplayName("Should throw VerificationExpiredException")
    void shouldThrowExpiredException() {
        Verification verification = createVerification();
        verification.setExpiresAt(Instant.now().minus(Duration.ofMinutes(1)));
        assertThrows(VerificationExpiredException.class, 
            () -> verification.confirm("123456", "user-info"));
    }

    @Test
    @DisplayName("Should throw VerificationForbiddenException")
    void shouldThrowForbiddenException() {
        Verification verification = createVerification();
        assertThrows(VerificationForbiddenException.class, 
            () -> verification.confirm("123456", "different-user"));
    }

    @Test
    @DisplayName("Should expire after 5 failed attempts")
    void shouldExpireAfter5FailedAttempts() {
        Verification verification = createVerification();
        for (int i = 0; i < 4; i++) {
            assertThrows(InvalidCodeException.class, 
                () -> verification.confirm("wrong", "user-info"));
        }
        assertThrows(VerificationExpiredException.class, 
            () -> verification.confirm("wrong", "user-info"));
    }

    @Test
    @DisplayName("Should generate domain event JSON")
    void shouldGenerateDomainEventJson() {
        Verification verification = createVerification();
        String json = verification.toDomainEventJson();
        assertNotNull(json);
        assertTrue(json.contains("123456"));
    }

    private Verification createVerification() {
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        return new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));
    }
}