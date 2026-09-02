package com.portfolio.assessment.eventworker.examinee;

import com.portfolio.assessment.eventworker.common.AssessmentContext;

public interface ExamineeAccessPolicy {
    boolean canSubmit(AssessmentContext context);
}

