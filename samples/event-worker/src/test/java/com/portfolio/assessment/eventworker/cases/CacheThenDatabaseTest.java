package com.portfolio.assessment.eventworker.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

class CacheThenDatabaseTest {
  @Test
  void databaseWaitsForAllCacheAttemptsIncludingFailedOnes() {
    var pending = Sinks.<Void>empty();
    var called = new AtomicBoolean();
    var work =
        new CacheThenDatabase()
            .save(
                List.of(
                    () -> pending.asMono(),
                    () -> Mono.error(new IllegalStateException("cache offline"))),
                () -> Mono.fromRunnable(() -> called.set(true)));
    StepVerifier.create(work)
        .then(() -> assertFalse(called.get()))
        .then(pending::tryEmitEmpty)
        .verifyComplete();
    assertTrue(called.get());
  }

  @Test
  void databaseFailureIsNotReportedAsSuccess() {
    var failure = new IllegalStateException("database offline");
    StepVerifier.create(new CacheThenDatabase().save(List.of(), () -> Mono.error(failure)))
        .expectErrorMatches(error -> error == failure)
        .verify();
  }
}
