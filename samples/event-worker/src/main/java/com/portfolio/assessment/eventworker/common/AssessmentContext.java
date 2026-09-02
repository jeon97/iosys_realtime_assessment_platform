package com.portfolio.assessment.eventworker.common;

public record AssessmentContext(
        String userId,
        String planId,
        String runType,
        String groupId,
        String accessType
) {
    public AssessmentContext {
        require(userId, "userId");
        require(planId, "planId");
        require(runType, "runType");
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}

