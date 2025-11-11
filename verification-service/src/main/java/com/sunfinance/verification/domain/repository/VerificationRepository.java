package com.sunfinance.verification.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.Verification;

public interface VerificationRepository {
    void save(Verification verification);
    Optional<Verification> findById(UUID id);
    Optional<Verification> findPendingBySubject(Subject subject);
}