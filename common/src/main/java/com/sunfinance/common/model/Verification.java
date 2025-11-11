package com.sunfinance.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunfinance.common.events.VerificationCreated;
import com.sunfinance.common.exceptions.InvalidCodeException;
import com.sunfinance.common.exceptions.VerificationExpiredException;
import com.sunfinance.common.exceptions.VerificationForbiddenException;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification")
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    private Subject subject;
    private String userInfo;
    private String code;
    private boolean confirmed = false;
    private Instant createdAt = Instant.now();
    private Instant expiresAt;    
    
	private int failedAttempts = 0;

    protected Verification() {}

    public Verification(Subject subject, String userInfo, String code, Duration ttl) {
        //this.id = UUID.randomUUID();
        this.subject = subject;
        this.userInfo = userInfo;
        this.code = code;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plus(Duration.ofMinutes(5));
    }

   
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void confirm(String providedCode, String providedUserInfo) throws VerificationExpiredException {
        if (isExpired()) {
            throw new VerificationExpiredException();          
        }
        if (!providedUserInfo.equals(this.userInfo)) {
            throw new VerificationForbiddenException();
        }
        if (confirmed) {
            return;
        }
        if (!this.code.equals(providedCode)) {
            failedAttempts++;
            if (failedAttempts >= 5) {
                throw new VerificationExpiredException();
            }
            throw new InvalidCodeException();
        }
        this.confirmed = true;
    }


    public String toDomainEventJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // чтобы сериализовать Instant
            return mapper.writeValueAsString(new VerificationCreated(
                    this.id,
                    this.code,
                    this.subject,
                    Instant.now()
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize VerificationCreated event", e);
        }
    }

    public UUID getId() { return id; }
    public Subject getSubject() { return subject; }
    public String getUserInfo() { return userInfo; }
    public String getCode() { return code; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isConfirmed() { return confirmed; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}