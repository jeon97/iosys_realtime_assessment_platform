package com.portfolio.assessment.eventworker.examinee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class MonitoringEventServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-15T00:00:00Z");

    @Test
    void updatesLatestCameraStateAndMarksEvent() {
        MemoryGateway gateway = new MemoryGateway();
        var result = service(gateway).process(event("event-1"), access()).block();
        assertEquals(MonitoringEventService.ProcessResult.PROCESSED, result);
        assertEquals(1, gateway.stateUpdates);
        assertEquals(1, gateway.published);
    }

    @Test
    void storesFallbackWhenPublishingFails() {
        MemoryGateway gateway = new MemoryGateway();
        gateway.failPublish = true;
        service(gateway).process(event("event-1"), access()).block();
        assertEquals(1, gateway.fallbacks);
        assertEquals(Set.of("event-1"), gateway.processed);
    }

    @Test
    void ignoresDuplicateAndRejectsWrongAssessment() {
        MemoryGateway gateway = new MemoryGateway();
        gateway.processed.add("event-1");
        assertEquals(MonitoringEventService.ProcessResult.DUPLICATE,
                service(gateway).process(event("event-1"), access()).block());
        assertThrows(SecurityException.class, () -> service(gateway).process(
                new MonitoringEventService.MonitoringEvent("e", "other", "user-1",
                        MonitoringEventService.EventType.CHAT, "message", NOW), access()));
    }

    private MonitoringEventService service(MemoryGateway gateway) {
        return new MonitoringEventService(gateway, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private MonitoringEventService.AssessmentAccess access() {
        return new MonitoringEventService.AssessmentAccess("user-1", Set.of("exam-1"));
    }

    private MonitoringEventService.MonitoringEvent event(String id) {
        return new MonitoringEventService.MonitoringEvent(id, "exam-1", "user-1",
                MonitoringEventService.EventType.CAMERA_STATE, "CONNECTED", NOW);
    }

    private static final class MemoryGateway implements MonitoringEventService.MonitoringGateway {
        private final Set<String> processed = new HashSet<>();
        private boolean failPublish;
        private int stateUpdates;
        private int published;
        private int fallbacks;
        public Mono<Boolean> isProcessed(String id) { return Mono.just(processed.contains(id)); }
        public Mono<Void> updateLatestState(String a, String e, String v, Instant at) { stateUpdates++; return Mono.empty(); }
        public Mono<Void> publish(MonitoringEventService.MonitoringEvent event) {
            if (failPublish) return Mono.error(new IllegalStateException("queue unavailable"));
            published++; return Mono.empty();
        }
        public Mono<Void> saveFallback(MonitoringEventService.MonitoringEvent event, String type) { fallbacks++; return Mono.empty(); }
        public Mono<Void> markProcessed(String id, Instant at) { processed.add(id); return Mono.empty(); }
    }
}
