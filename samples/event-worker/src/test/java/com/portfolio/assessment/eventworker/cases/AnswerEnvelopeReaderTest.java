package com.portfolio.assessment.eventworker.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class AnswerEnvelopeReaderTest {
  @Test
  void readsOptionalPreviousAnswerFromNestedData() {
    var reader = new AnswerEnvelopeReader();
    var result =
        reader.read(
            Map.of(
                "data",
                Map.of(
                    "current",
                    Map.of("number", 2),
                    "progress",
                    Map.of("answered", 2),
                    "previous",
                    Map.of("number", 1))));
    assertEquals(2, result.current().get("number"));
    assertEquals(1, result.previous().orElseThrow().get("number"));
    assertThrows(UnsupportedOperationException.class, () -> result.current().put("number", 3));
  }

  @Test
  void missingNestedDataIsRejectedWithoutNullDereference() {
    var reader = new AnswerEnvelopeReader();
    assertThrows(IllegalArgumentException.class, () -> reader.read(Map.of("current", Map.of())));
    assertThrows(
        IllegalArgumentException.class,
        () -> reader.read(Map.of("data", Map.of("current", Map.of()))));
    assertTrue(
        reader
            .read(Map.of("data", Map.of("current", Map.of(), "progress", Map.of())))
            .previous()
            .isEmpty());
  }
}
