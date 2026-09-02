package com.portfolio.assessment.eventworker.examinee;

public final class AnswerSubmissionService {
    private final ExamineeAccessPolicy accessPolicy;
    private final AnswerEventPublisher publisher;
    private final AnswerFallbackRepository fallbackRepository;

    public AnswerSubmissionService(
            ExamineeAccessPolicy accessPolicy,
            AnswerEventPublisher publisher,
            AnswerFallbackRepository fallbackRepository
    ) {
        this.accessPolicy = accessPolicy;
        this.publisher = publisher;
        this.fallbackRepository = fallbackRepository;
    }

    public SubmissionResult submit(AnswerCommand command) {
        validate(command);
        if (!accessPolicy.canSubmit(command.context())) {
            throw new IllegalStateException("examinee cannot submit to this assessment");
        }
        try {
            publisher.publish(command);
            return SubmissionResult.QUEUED;
        } catch (RuntimeException publishFailure) {
            fallbackRepository.save(command);
            return SubmissionResult.FALLBACK_STORED;
        }
    }

    private void validate(AnswerCommand command) {
        if (command.commandId() == null || command.commandId().isBlank()) {
            throw new IllegalArgumentException("commandId is required");
        }
        if (command.questionId() == null || command.questionId().isBlank()) {
            throw new IllegalArgumentException("questionId is required");
        }
        if (command.submittedAt() == null) {
            throw new IllegalArgumentException("submittedAt is required");
        }
    }

    public enum SubmissionResult {
        QUEUED, FALLBACK_STORED
    }
}

