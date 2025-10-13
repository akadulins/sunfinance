package com.sunfinance.common.events;

public interface DomainEventPublisher {
    void publish(Object event);
}