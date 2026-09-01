package com.portfolio.assessment.eventworker.service;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import com.portfolio.assessment.eventworker.port.EventRepository;
import com.portfolio.assessment.eventworker.port.IdempotencyStore;
import com.portfolio.assessment.eventworker.port.RetryQueue;
import com.portfolio.assessment.eventworker.port.StateStore;
import reactor.core.publisher.Mono;

public class EventProcessor {

    private final IdempotencyStore idempotencyStore;
    private final StateStore stateStore;
    private final EventRepository eventRepository;
    private final RetryQueue retryQueue;

    public EventProcessor(
            IdempotencyStore idempotencyStore,
            StateStore stateStore,
            EventRepository eventRepository,
            RetryQueue retryQueue
    ) {
        this.idempotencyStore = idempotencyStore;
        this.stateStore = stateStore;
        this.eventRepository = eventRepository;
        this.retryQueue = retryQueue;
    }

    public Mono<ProcessingResult> process(AssessmentEvent event) {
        return idempotencyStore.claim(event.eventId())
                .flatMap(claimed -> claimed ? persist(event) : Mono.just(ProcessingResult.duplicate(event.eventId())));
    }

    private Mono<ProcessingResult> persist(AssessmentEvent event) {
        return stateStore.apply(event)
                .then(eventRepository.save(event))
                .thenReturn(ProcessingResult.accepted(event.eventId()))
                .onErrorResume(error -> retryQueue.enqueue(event)
                        .thenReturn(ProcessingResult.queuedForRetry(event.eventId())));
    }

    public record ProcessingResult(String eventId, Status status) {
        static ProcessingResult accepted(java.util.UUID eventId) {
            return new ProcessingResult(eventId.toString(), Status.ACCEPTED);
        }

        static ProcessingResult duplicate(java.util.UUID eventId) {
            return new ProcessingResult(eventId.toString(), Status.DUPLICATE);
        }

        static ProcessingResult queuedForRetry(java.util.UUID eventId) {
            return new ProcessingResult(eventId.toString(), Status.QUEUED_FOR_RETRY);
        }
    }

    public enum Status {
        ACCEPTED,
        DUPLICATE,
        QUEUED_FOR_RETRY
    }
}
