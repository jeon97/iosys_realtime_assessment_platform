package com.portfolio.assessment.eventworker.examinee;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import reactor.core.publisher.Mono;

public final class MonitoringEventService {
    private final MonitoringGateway gateway;
    private final Clock clock;

    public MonitoringEventService(MonitoringGateway gateway, Clock clock) {
        this.gateway = gateway;
        this.clock = clock;
    }

    public Mono<ProcessResult> process(MonitoringEvent event, AssessmentAccess access) {
        validate(event, access);
        return gateway.isProcessed(event.eventId())
                .flatMap(processed -> processed
                        ? Mono.just(ProcessResult.DUPLICATE)
                        : apply(event).thenReturn(ProcessResult.PROCESSED));
    }

    private Mono<Void> apply(MonitoringEvent event) {
        Mono<Void> stateUpdate = event.type() == EventType.CAMERA_STATE
                ? gateway.updateLatestState(event.assessmentId(), event.examineeId(), event.value(), event.occurredAt())
                : Mono.empty();
        return stateUpdate.then(gateway.publish(event))
                .onErrorResume(error -> gateway.saveFallback(event, error.getClass().getSimpleName()))
                .then(gateway.markProcessed(event.eventId(), clock.instant()));
    }

    private void validate(MonitoringEvent event, AssessmentAccess access) {
        if (!access.assessmentIds().contains(event.assessmentId())) {
            throw new SecurityException("assessment access denied");
        }
        if (!access.examineeId().equals(event.examineeId())) {
            throw new SecurityException("examinee identity mismatch");
        }
        if (event.eventId() == null || event.eventId().isBlank()
                || event.value() == null || event.value().isBlank()
                || event.occurredAt() == null) {
            throw new IllegalArgumentException("monitoring event is incomplete");
        }
    }

    public enum EventType { CAMERA_STATE, CHAT, SUSPICIOUS_ACTIVITY }
    public enum ProcessResult { PROCESSED, DUPLICATE }
    public record AssessmentAccess(String examineeId, Set<String> assessmentIds) {}
    public record MonitoringEvent(String eventId, String assessmentId, String examineeId,
                                  EventType type, String value, Instant occurredAt) {}

    public interface MonitoringGateway {
        Mono<Boolean> isProcessed(String eventId);
        Mono<Void> updateLatestState(String assessmentId, String examineeId, String value, Instant occurredAt);
        Mono<Void> publish(MonitoringEvent event);
        Mono<Void> saveFallback(MonitoringEvent event, String failureType);
        Mono<Void> markProcessed(String eventId, Instant processedAt);
    }
}
