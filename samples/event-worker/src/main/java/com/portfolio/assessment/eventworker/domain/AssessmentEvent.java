package com.portfolio.assessment.eventworker.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AssessmentEvent(
        UUID eventId,
        String participantId,
        EventType type,
        Instant occurredAt,
        Map<String, String> payload
) {
    public static AssessmentEvent create(String participantId, EventType type, Map<String, String> payload) {
        return new AssessmentEvent(UUID.randomUUID(), participantId, type, Instant.now(), Map.copyOf(payload));
    }
}
