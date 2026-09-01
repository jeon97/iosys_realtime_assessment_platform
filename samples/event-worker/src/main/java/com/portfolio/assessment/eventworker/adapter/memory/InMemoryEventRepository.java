package com.portfolio.assessment.eventworker.adapter.memory;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import com.portfolio.assessment.eventworker.port.EventRepository;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import reactor.core.publisher.Mono;

public class InMemoryEventRepository implements EventRepository {

    private final List<AssessmentEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> save(AssessmentEvent event) {
        return Mono.fromRunnable(() -> events.add(event));
    }
}
