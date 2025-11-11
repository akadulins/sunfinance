package com.sunfinance.verification.api.mapper;

import com.sunfinance.common.model.*;
import com.sunfinance.verification.api.model.*;
import com.sunfinance.verification.application.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VerificationApiMapperTest {

    private final VerificationApiMapper mapper = new VerificationApiMapper();

    @Test
    @DisplayName("Should map to CreateVerificationCommand")
    void shouldMapToCommand() {
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        CreateVerificationRequest request = new CreateVerificationRequest(subject);

        CreateVerificationCommand cmd = mapper.toCommand(request, "user-info");

        assertEquals("test@example.com", cmd.identity());
        assertEquals("user-info", cmd.userInfo());
    }

    @Test
    @DisplayName("Should map to ConfirmVerificationCommand")
    void shouldMapToConfirmCommand() {
        UUID id = UUID.randomUUID();
        ConfirmVerificationRequest request = new ConfirmVerificationRequest("123456");

        ConfirmVerificationCommand cmd = mapper.toConfirmCommand(id, request, "user-info");

        assertEquals(id, cmd.verificationId());
        assertEquals("123456", cmd.code());
    }

    @Test
    @DisplayName("Should map to VerificationResponse")
    void shouldMapToResponse() {
        Subject subject = new Subject("test@example.com", SubjectType.EMAIL_CONFIRMATION);
        Verification v = new Verification(subject, "user-info", "123456", Duration.ofMinutes(5));

        VerificationResponse response = mapper.toResponse(v);

        assertEquals(v.getId(), response.id());
        assertEquals("test@example.com", response.subjectIdentity());
    }
}