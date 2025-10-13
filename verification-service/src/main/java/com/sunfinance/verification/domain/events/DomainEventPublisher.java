package com.sunfinance.verification.domain.events;

public interface DomainEventPublisher {
    void publish(Object event);
}
