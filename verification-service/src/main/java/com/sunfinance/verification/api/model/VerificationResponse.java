package com.sunfinance.verification.api.model;

import java.time.Instant;
import java.util.UUID;

public record VerificationResponse(
        UUID id,
        String subjectIdentity,
        String subjectType,
        String code,
        Instant expiresAt,
        boolean confirmed
) {}