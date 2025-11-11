package com.sunfinance.verification.infrastructure.outbox;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import com.sunfinance.common.model.OutboxEvent;
import com.sunfinance.verification.domain.repository.OutboxEventRepository;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(OutboxEvent event) {
        entityManager.persist(event);
    }

    @Override
    public List<OutboxEvent> findTop50Unprocessed() {
        return entityManager.createQuery("""
                SELECT e FROM OutboxEvent e
                WHERE e.processed = false
                ORDER BY e.createdAt ASC
                """, OutboxEvent.class)
                .setMaxResults(50)
                .getResultList();
    }

    @Override
    public void markProcessed(UUID id) {
        entityManager.createQuery("""
                UPDATE OutboxEvent e
                SET e.processed = true
                WHERE e.id = :id
                """)
                .setParameter("id", id)
                .executeUpdate();
    }
}