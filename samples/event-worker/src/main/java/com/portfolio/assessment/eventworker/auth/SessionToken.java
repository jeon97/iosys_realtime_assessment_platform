package com.portfolio.assessment.eventworker.auth;

import java.time.Instant;
import java.util.Set;

public record SessionToken(
        String tokenId,
        String userId,
        String assessmentId,
        Set<String> roles,
        Instant expiresAt
) {
    public SessionToken {
        roles = Set.copyOf(roles);
    }
}

