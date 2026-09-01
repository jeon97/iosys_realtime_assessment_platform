package com.portfolio.assessment.eventworker.adapter.memory;

import com.portfolio.assessment.eventworker.port.IdempotencyStore;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import reactor.core.publisher.Mono;

public class InMemoryIdempotencyStore implements IdempotencyStore {

    private final Set<UUID> processedIds = ConcurrentHashMap.newKeySet();

    @Override
    public Mono<Boolean> claim(UUID eventId) {
        return Mono.fromSupplier(() -> processedIds.add(eventId));
    }
}
