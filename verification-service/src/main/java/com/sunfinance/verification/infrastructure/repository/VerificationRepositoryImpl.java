package com.sunfinance.verification.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import com.sunfinance.common.model.Subject;
import com.sunfinance.common.model.Verification;
import com.sunfinance.verification.domain.repository.VerificationRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class VerificationRepositoryImpl implements VerificationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Verification verification) {
    	
        if (verification.getId() == null){
            entityManager.persist(verification);
        } else {
            entityManager.merge(verification);
        }
    }

    @Override
    public Optional<Verification> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Verification.class, id));
    }

    @Override
    public Optional<Verification> findPendingBySubject(Subject subject) {
        return entityManager.createQuery("""
                SELECT v FROM Verification v 
                WHERE v.subject.identity = :identity 
                AND v.subject.type = :type 
                AND v.confirmed = false
                """, Verification.class)
            .setParameter("identity", subject.getIdentity())
            .setParameter("type", subject.getType())
            .getResultStream()
            .findFirst();
    }
}
