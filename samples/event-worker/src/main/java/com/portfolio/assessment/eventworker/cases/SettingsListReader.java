package com.portfolio.assessment.eventworker.cases;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/** Public reconstruction: ordinary and once-encoded JSON arrays from plan settings. */
public final class SettingsListReader {
  private final ObjectMapper mapper = new ObjectMapper();

  public List<String> read(String input) {
    if (input == null || input.isBlank()) return List.of();
    try {
      JsonNode value = mapper.readTree(input);
      if (value.isTextual()) value = mapper.readTree(value.textValue());
      if (value == null || !value.isArray()) return List.of();
      List<String> result = new ArrayList<>();
      for (JsonNode item : value) {
        // Explicit string-only contract is an additional sample rule.
        if (!item.isTextual()) return List.of();
        result.add(item.textValue());
      }
      return List.copyOf(result);
    } catch (java.io.IOException error) {
      return List.of();
    }
  }
}
