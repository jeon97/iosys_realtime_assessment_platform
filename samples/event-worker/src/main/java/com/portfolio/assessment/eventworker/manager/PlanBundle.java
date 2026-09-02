package com.portfolio.assessment.eventworker.manager;

import java.util.List;
import java.util.Map;

public record PlanBundle(
        Map<String, Object> plan,
        Map<String, Object> assessmentPackage,
        List<Map<String, Object>> users
) {
    public PlanBundle {
        plan = Map.copyOf(plan);
        assessmentPackage = Map.copyOf(assessmentPackage);
        users = List.copyOf(users);
    }
}

