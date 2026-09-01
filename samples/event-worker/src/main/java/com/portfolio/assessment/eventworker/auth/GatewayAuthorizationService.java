package com.portfolio.assessment.eventworker.auth;

import java.util.Set;

public final class GatewayAuthorizationService {
    private final SessionTokenService tokens;
    private final Set<String> publicPaths;

    public GatewayAuthorizationService(SessionTokenService tokens, Set<String> publicPaths) {
        this.tokens = tokens;
        this.publicPaths = Set.copyOf(publicPaths);
    }

    public Authorization authorize(String path, String authorizationHeader) {
        if (publicPaths.contains(path)) {
            return Authorization.anonymous();
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new SessionTokenService.AuthenticationException("bearer token is required");
        }
        SessionToken token = tokens.authenticate(authorizationHeader.substring(7));
        return new Authorization(token.userId(), token.assessmentId(), token.roles(), false);
    }

    public record Authorization(
            String userId,
            String assessmentId,
            Set<String> roles,
            boolean publicRequest
    ) {
        private static Authorization anonymous() {
            return new Authorization(null, null, Set.of(), true);
        }
    }
}

