package com.sunfinance.common.events;

import java.time.Instant;
import java.util.UUID;

import com.sunfinance.common.model.Subject;

public record VerificationCreated(UUID id, String code, Subject subject, Instant occurredOn) {
    public VerificationCreated(UUID id, String code, Subject subject) {
        this(id, code, subject, Instant.now());
    }
}