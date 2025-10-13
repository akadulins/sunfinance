package com.sunfinance.common.dto;

import java.time.Instant;
import java.util.UUID;

import com.sunfinance.common.model.Subject;

public record NotificationCreated(UUID id, Subject subject, Instant occurredOn) {
    public NotificationCreated(UUID id, Subject subject) {
        this(id, subject, Instant.now());
    }
}