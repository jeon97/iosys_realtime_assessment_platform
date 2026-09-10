package com.portfolio.assessment.eventworker.cases;

import java.util.List;
import java.util.function.Supplier;
import reactor.core.publisher.Mono;

/** Cache attempts finish before database work starts; this is not a distributed transaction. */
public final class CacheThenDatabase {
  public Mono<Void> save(
      List<Supplier<Mono<Void>>> cacheWrites, Supplier<Mono<Void>> databaseWrite) {
    var attempts =
        cacheWrites.stream()
            .map(write -> Mono.defer(write).onErrorResume(error -> Mono.empty()))
            .toList();
    // Database errors remain visible in this sample; the source worker also contained error
    // swallowing.
    return Mono.when(attempts).then(Mono.defer(databaseWrite));
  }
}
