package com.portfolio.assessment.eventworker.service;

import com.portfolio.assessment.eventworker.adapter.memory.InMemoryIdempotencyStore;
import com.portfolio.assessment.eventworker.adapter.memory.InMemoryRetryQueue;
import com.portfolio.assessment.eventworker.adapter.memory.InMemoryStateStore;
import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import com.portfolio.assessment.eventworker.domain.EventType;
import com.portfolio.assessment.eventworker.port.EventRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class EventProcessorTest {

    @Test
    void stores_a_new_event_and_rejects_the_same_event_id() {
        EventProcessor processor = new EventProcessor(
                new InMemoryIdempotencyStore(),
                new InMemoryStateStore(),
                event -> Mono.empty(),
                new InMemoryRetryQueue()
        );
        AssessmentEvent event = AssessmentEvent.create("participant-101", EventType.ANSWER_SAVED, Map.of("answer", "A"));

        StepVerifier.create(processor.process(event))
                .expectNextMatches(result -> result.status() == EventProcessor.Status.ACCEPTED)
                .verifyComplete();

        StepVerifier.create(processor.process(event))
                .expectNextMatches(result -> result.status() == EventProcessor.Status.DUPLICATE)
                .verifyComplete();
    }

    @Test
    void queues_an_event_when_persistence_fails() {
        EventRepository failingRepository = event -> Mono.error(new IllegalStateException("storage unavailable"));
        EventProcessor processor = new EventProcessor(
                new InMemoryIdempotencyStore(),
                new InMemoryStateStore(),
                failingRepository,
                new InMemoryRetryQueue()
        );
        AssessmentEvent event = AssessmentEvent.create("participant-102", EventType.PROGRESS_UPDATED, Map.of());

        StepVerifier.create(processor.process(event))
                .expectNextMatches(result -> result.status() == EventProcessor.Status.QUEUED_FOR_RETRY)
                .verifyComplete();
    }
}
