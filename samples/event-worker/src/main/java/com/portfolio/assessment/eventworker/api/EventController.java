package com.portfolio.assessment.eventworker.api;

import com.portfolio.assessment.eventworker.domain.AssessmentEvent;
import com.portfolio.assessment.eventworker.domain.EventType;
import com.portfolio.assessment.eventworker.port.EventRepository;
import com.portfolio.assessment.eventworker.port.IdempotencyStore;
import com.portfolio.assessment.eventworker.port.RetryQueue;
import com.portfolio.assessment.eventworker.port.StateStore;
import com.portfolio.assessment.eventworker.service.EventProcessor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventProcessor eventProcessor;
    private final StateStore stateStore;

    public EventController(
            IdempotencyStore idempotencyStore,
            StateStore stateStore,
            EventRepository eventRepository,
            RetryQueue retryQueue
    ) {
        this.eventProcessor = new EventProcessor(idempotencyStore, stateStore, eventRepository, retryQueue);
        this.stateStore = stateStore;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<EventProcessor.ProcessingResult> publish(@Valid @RequestBody EventRequest request) {
        AssessmentEvent event = AssessmentEvent.create(request.participantId(), request.type(), request.payload());
        return eventProcessor.process(event);
    }

    @GetMapping("/participants/{participantId}/state")
    public Mono<Map<String, String>> state(@PathVariable String participantId) {
        return stateStore.findByParticipantId(participantId).defaultIfEmpty(Map.of());
    }

    public record EventRequest(
            @NotBlank String participantId,
            @NotNull EventType type,
            Map<String, String> payload
    ) {
        public EventRequest {
            payload = payload == null ? Map.of() : Map.copyOf(payload);
        }
    }
}
