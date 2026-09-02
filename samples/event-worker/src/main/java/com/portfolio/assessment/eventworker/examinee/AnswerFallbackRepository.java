package com.portfolio.assessment.eventworker.examinee;

public interface AnswerFallbackRepository {
    void save(AnswerCommand command);
}

