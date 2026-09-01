package com.portfolio.assessment.eventworker.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionTokenServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-15T00:00:00Z");

    @Test
    void authorizesActiveSessionAndPassesIdentityToGateway() {
        SessionToken decoded = new SessionToken(
                "token-1", "user-1", "assessment-1", Set.of("EXAMINEE"), NOW.plusSeconds(60)
        );
        SessionTokenService tokenService = new SessionTokenService(
                token -> decoded, (userId, tokenId) -> true, Clock.fixed(NOW, ZoneOffset.UTC)
        );
        GatewayAuthorizationService gateway = new GatewayAuthorizationService(
                tokenService, Set.of("/health")
        );

        var authorization = gateway.authorize("/api/answers", "Bearer signed-token");

        assertEquals("user-1", authorization.userId());
        assertEquals("assessment-1", authorization.assessmentId());
    }

    @Test
    void rejectsExpiredOrInactiveSession() {
        SessionToken expired = new SessionToken(
                "token-1", "user-1", "assessment-1", Set.of(), NOW
        );
        SessionTokenService expiredService = new SessionTokenService(
                token -> expired, (userId, tokenId) -> true, Clock.fixed(NOW, ZoneOffset.UTC)
        );
        assertThrows(SessionTokenService.AuthenticationException.class,
                () -> expiredService.authenticate("token"));

        SessionToken activeTime = new SessionToken(
                "token-2", "user-1", "assessment-1", Set.of(), NOW.plusSeconds(60)
        );
        SessionTokenService inactiveService = new SessionTokenService(
                token -> activeTime, (userId, tokenId) -> false, Clock.fixed(NOW, ZoneOffset.UTC)
        );
        assertThrows(SessionTokenService.AuthenticationException.class,
                () -> inactiveService.authenticate("token"));
    }

    @Test
    void allowsConfiguredPublicPathWithoutToken() {
        SessionTokenService tokenService = new SessionTokenService(
                token -> { throw new AssertionError(); },
                (userId, tokenId) -> false,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
        GatewayAuthorizationService gateway = new GatewayAuthorizationService(
                tokenService, Set.of("/health")
        );

        assertTrue(gateway.authorize("/health", null).publicRequest());
    }
}

