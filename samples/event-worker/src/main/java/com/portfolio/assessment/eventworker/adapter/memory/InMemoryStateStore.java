package com.portfolio.assessment.eventworker.adapter.memory;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import com.portfolio.assessment.eventworker.port.StateStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import reactor.core.publisher.Mono;

public class InMemoryStateStore implements StateStore {

    private final Map<String, Map<String, String>> states = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> apply(AssessmentEvent event) {
        return Mono.fromRunnable(() -> states.compute(event.participantId(), (id, current) -> {
            Map<String, String> next = new ConcurrentHashMap<>();
            if (current != null) {
                next.putAll(current);
            }
            next.put("lastEventType", event.type().name());
            next.put("lastEventAt", event.occurredAt().toString());
            next.putAll(event.payload());
            return next;
        }));
    }

    @Override
    public Mono<Map<String, String>> findByParticipantId(String participantId) {
        return Mono.justOrEmpty(states.get(participantId)).map(Map::copyOf);
    }
}
