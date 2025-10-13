package com.sunfinance.common.events;

import java.time.Instant;
import java.util.UUID;

import com.sunfinance.common.model.Subject;

public record VerificationConfirmed(UUID id,  String code, Subject subject, Instant occurredOn) {
    public VerificationConfirmed(UUID id,   String code, Subject subject) {
        this(id, code, subject,  Instant.now());
    }
}