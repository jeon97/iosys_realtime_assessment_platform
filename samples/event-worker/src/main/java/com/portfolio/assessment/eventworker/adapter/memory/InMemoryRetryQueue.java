package com.portfolio.assessment.eventworker.adapter.memory;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import com.portfolio.assessment.eventworker.port.RetryQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import reactor.core.publisher.Mono;

public class InMemoryRetryQueue implements RetryQueue {

    private final Queue<AssessmentEvent> queuedEvents = new ConcurrentLinkedQueue<>();

    @Override
    public Mono<Void> enqueue(AssessmentEvent event) {
        return Mono.fromRunnable(() -> queuedEvents.offer(event));
    }
}
