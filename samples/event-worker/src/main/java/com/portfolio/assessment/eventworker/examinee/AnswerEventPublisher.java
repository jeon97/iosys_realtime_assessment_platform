package com.portfolio.assessment.eventworker.examinee;

public interface AnswerEventPublisher {
    void publish(AnswerCommand command);
}

