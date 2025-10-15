package com.sunfinance.common.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.sunfinance.common.exceptions.InvalidCodeException;
import com.sunfinance.common.exceptions.VerificationExpiredException;
import com.sunfinance.common.exceptions.VerificationForbiddenException;

import jakarta.persistence.*;

@Entity
@Table(name = "verifications")
public class Verification {
	@Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
	
	public Verification() {}
   

	@Embedded
    private Subject subject;
    private boolean confirmed = false;
    private String code;
    private String userInfo;
    private Instant createdAt;
    private Instant expiresAt;
    
    private int failedAttempts = 0;
    
    public Verification(Subject subject, String userInfo, String code, Duration ttl) {
        this.id = UUID.randomUUID();
        this.subject = subject;
        this.userInfo = userInfo;
        this.code = code;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plus(ttl);
    }
    
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
    
    public Subject getSubject() {
		return subject;
	}
	
	public boolean isConfirmed() {
		return confirmed;
	}
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
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
}
