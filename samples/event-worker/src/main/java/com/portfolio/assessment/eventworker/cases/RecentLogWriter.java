package com.portfolio.assessment.eventworker.cases;

import java.time.Duration;
import reactor.core.publisher.Mono;

/** Models newest-first append, bounded retention and optional expiration. */
public final class RecentLogWriter {
  public interface Store {
    Mono<Void> prepend(String scope, String entry);

    Mono<Void> keepFirst(String scope, int count);

    Mono<Void> expire(String scope, Duration lifetime);
  }

  private final Store store;
  private final int limit;

  public RecentLogWriter(Store store, int limit) {
    if (limit < 1) throw new IllegalArgumentException("positive limit required");
    this.store = store;
    this.limit = limit;
  }

  public Mono<Boolean> append(String scope, String entry, Duration lifetime) {
    if (lifetime != null && (lifetime.isNegative() || lifetime.isZero()))
      return Mono.error(new IllegalArgumentException("positive lifetime required"));
    return Mono.defer(() -> store.prepend(scope, entry))
        .then(Mono.defer(() -> store.keepFirst(scope, limit)))
        .then(Mono.defer(() -> lifetime == null ? Mono.empty() : store.expire(scope, lifetime)))
        .thenReturn(true)
        .onErrorReturn(false);
  }
}
