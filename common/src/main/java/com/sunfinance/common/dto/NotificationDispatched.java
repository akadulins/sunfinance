package com.sunfinance.common.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDispatched(UUID id, String recipient, Instant occurredOn) {}
