package com.sunfinance.verification.domain.repository;

import java.util.List;
import java.util.UUID;
import com.sunfinance.common.model.OutboxEvent;

public interface OutboxEventRepository {

	void save(OutboxEvent event);

	List<OutboxEvent> findTop50Unprocessed();

	void markProcessed(UUID id);
}
