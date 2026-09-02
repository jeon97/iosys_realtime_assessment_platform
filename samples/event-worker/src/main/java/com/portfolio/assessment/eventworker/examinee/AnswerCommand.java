package com.portfolio.assessment.eventworker.examinee;

import com.portfolio.assessment.eventworker.common.AssessmentContext;
import java.time.Instant;

public record AnswerCommand(
        String commandId,
        AssessmentContext context,
        String questionId,
        String answer,
        boolean testCompleted,
        Instant submittedAt
) {
}

