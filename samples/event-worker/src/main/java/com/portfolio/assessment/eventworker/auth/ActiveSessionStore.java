package com.portfolio.assessment.eventworker.auth;

public interface ActiveSessionStore {
    boolean isActive(String userId, String tokenId);
}

