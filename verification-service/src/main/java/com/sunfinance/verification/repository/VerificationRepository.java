
package com.sunfinance.verification.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunfinance.common.model.SubjectType;
import com.sunfinance.common.model.Verification;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {
    Optional<Verification> findBySubjectIdentityAndSubjectTypeAndConfirmedIsFalse(String identity, SubjectType type);
}