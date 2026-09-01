package com.portfolio.assessment.eventworker.port;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface IdempotencyStore {
    Mono<Boolean> claim(UUID eventId);
}
