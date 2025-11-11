package com.sunfinance.common.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String topic;

	private String eventType;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String payload;

	private boolean processed = false;

	@Column(nullable = false)
	private Instant createdAt = Instant.now();

	public OutboxEvent() {}

	public OutboxEvent(String topic, String payload) {
		this.topic = topic;
		this.payload = payload;
	}

	public UUID getId() { return id; }
	public String getTopic() { return topic; }
	public String getEventType() { return eventType; }
	public String getPayload() { return payload; }
	public Instant getCreatedAt() { return createdAt; }
	public boolean isProcessed() { return processed; }

}
