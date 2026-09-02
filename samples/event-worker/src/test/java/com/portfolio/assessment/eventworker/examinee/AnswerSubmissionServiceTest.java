package com.portfolio.assessment.eventworker.examinee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.portfolio.assessment.eventworker.common.AssessmentContext;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AnswerSubmissionServiceTest {
    @Test
    void publishesValidatedAnswerEvent() {
        AtomicInteger published = new AtomicInteger();
        AnswerSubmissionService service = new AnswerSubmissionService(
                context -> true,
                command -> published.incrementAndGet(),
                command -> { throw new AssertionError(); }
        );

        assertEquals(AnswerSubmissionService.SubmissionResult.QUEUED, service.submit(command()));
        assertEquals(1, published.get());
    }

    @Test
    void storesAnswerDirectlyWhenQueuePublishingFails() {
        AtomicInteger fallbackSaved = new AtomicInteger();
        AnswerSubmissionService service = new AnswerSubmissionService(
                context -> true,
                command -> { throw new IllegalStateException("queue unavailable"); },
                command -> fallbackSaved.incrementAndGet()
        );

        assertEquals(
                AnswerSubmissionService.SubmissionResult.FALLBACK_STORED,
                service.submit(command())
        );
        assertEquals(1, fallbackSaved.get());
    }

    @Test
    void rejectsUserOutsideAssessmentContext() {
        AnswerSubmissionService service = new AnswerSubmissionService(
                context -> false, command -> { }, command -> { }
        );

        assertThrows(IllegalStateException.class, () -> service.submit(command()));
    }

    private AnswerCommand command() {
        return new AnswerCommand(
                "command-1",
                new AssessmentContext("user-1", "plan-1", "TEST", "group-1", "WEB"),
                "question-1", "A", false, Instant.parse("2026-01-15T00:00:00Z")
        );
    }
}

