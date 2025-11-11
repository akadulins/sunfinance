package com.sunfinance.common.events;

import java.time.Instant;
import java.util.UUID;

import com.sunfinance.common.model.Subject;

public class VerificationCreated {
    private UUID verificationId;
    private String code;
    private Subject subject;
    private Instant occurredOn;

    public VerificationCreated(UUID verificationId, String code, Subject subject, Instant occurredOn) {
        this.verificationId = verificationId;
        this.code = code;
        this.subject = subject;
        this.occurredOn = occurredOn;
    }

    public UUID getVerificationId() { return verificationId; }
    public String getCode() { return code; }
    public Subject getSubject() { return subject; }
    public Instant getOccurredOn() { return occurredOn; }
}