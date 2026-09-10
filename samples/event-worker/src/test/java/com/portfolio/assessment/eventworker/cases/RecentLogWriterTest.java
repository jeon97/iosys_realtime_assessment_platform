package com.portfolio.assessment.eventworker.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class RecentLogWriterTest {
  static class Memory implements RecentLogWriter.Store {
    final List<String> rows = new ArrayList<>();
    Duration expiration;

    public Mono<Void> prepend(String scope, String row) {
      return Mono.fromRunnable(() -> rows.add(0, row));
    }

    public Mono<Void> keepFirst(String scope, int count) {
      return Mono.fromRunnable(
          () -> {
            if (rows.size() > count) rows.subList(count, rows.size()).clear();
          });
    }

    public Mono<Void> expire(String scope, Duration ttl) {
      return Mono.fromRunnable(() -> expiration = ttl);
    }
  }

  @Test
  void retainsNewestEntriesAndAppliesExpiration() {
    var store = new Memory();
    var writer = new RecentLogWriter(store, 2);
    writer.append("demo", "a", null).block();
    writer.append("demo", "b", null).block();
    assertTrue(writer.append("demo", "c", Duration.ofMinutes(5)).block());
    assertEquals(List.of("c", "b"), store.rows);
    assertEquals(Duration.ofMinutes(5), store.expiration);
  }

  @Test
  void failedAppendDoesNotRunRetention() {
    var store =
        new Memory() {
          public Mono<Void> prepend(String s, String r) {
            return Mono.error(new IllegalStateException());
          }

          public Mono<Void> keepFirst(String s, int n) {
            fail("must not trim");
            return Mono.empty();
          }
        };
    assertFalse(new RecentLogWriter(store, 2).append("demo", "a", null).block());
  }
}
