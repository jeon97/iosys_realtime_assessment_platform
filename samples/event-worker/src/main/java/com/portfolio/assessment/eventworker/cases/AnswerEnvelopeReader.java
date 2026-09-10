package com.portfolio.assessment.eventworker.cases;

import java.util.Map;
import java.util.Optional;

/** Separates the current answer, progress and optional previous answer inside data. */
public final class AnswerEnvelopeReader {
  public record Submission(
      Map<String, Object> current,
      Map<String, Object> progress,
      Optional<Map<String, Object>> previous) {}

  public Submission read(Map<String, Object> request) {
    var data = object(request.get("data"));
    var current = object(data.get("current"));
    var progress = object(data.get("progress"));
    return new Submission(
        current, progress, Optional.ofNullable(data.get("previous")).map(this::object));
  }

  private Map<String, Object> object(Object value) {
    if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("object required");
    var result = new java.util.LinkedHashMap<String, Object>();
    for (var entry : map.entrySet()) {
      if (!(entry.getKey() instanceof String key) || entry.getValue() == null)
        throw new IllegalArgumentException("non-null string-keyed fields required");
      result.put(key, entry.getValue());
    }
    return java.util.Collections.unmodifiableMap(result);
  }
}
