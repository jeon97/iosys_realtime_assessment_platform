package com.portfolio.assessment.eventworker.port;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface StateStore {
    Mono<Void> apply(AssessmentEvent event);

    Mono<Map<String, String>> findByParticipantId(String participantId);
}
