package com.portfolio.assessment.eventworker.port;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import reactor.core.publisher.Mono;

public interface EventRepository {
    Mono<Void> save(AssessmentEvent event);
}
