package com.portfolio.assessment.eventworker.auth;

public interface TokenDecoder {
    SessionToken decodeAndVerify(String token);
}

