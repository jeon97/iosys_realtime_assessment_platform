package com.portfolio.assessment.eventworker.auth;

import java.time.Clock;

public final class SessionTokenService {
    private final TokenDecoder decoder;
    private final ActiveSessionStore sessions;
    private final Clock clock;

    public SessionTokenService(TokenDecoder decoder, ActiveSessionStore sessions, Clock clock) {
        this.decoder = decoder;
        this.sessions = sessions;
        this.clock = clock;
    }

    public SessionToken authenticate(String token) {
        SessionToken decoded = decoder.decodeAndVerify(token);
        if (!clock.instant().isBefore(decoded.expiresAt())) {
            throw new AuthenticationException("token is expired");
        }
        if (!sessions.isActive(decoded.userId(), decoded.tokenId())) {
            throw new AuthenticationException("session is not active");
        }
        return decoded;
    }

    public static final class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}

