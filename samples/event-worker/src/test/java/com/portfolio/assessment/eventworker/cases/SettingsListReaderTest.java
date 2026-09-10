package com.portfolio.assessment.eventworker.cases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SettingsListReaderTest {
  @Test
  void acceptsBothStoredRepresentations() throws Exception {
    var reader = new SettingsListReader();
    var raw = "[\"ko\",\"en\"]";
    var encoded = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(raw);
    assertEquals(java.util.List.of("ko", "en"), reader.read(raw));
    assertEquals(reader.read(raw), reader.read(encoded));
  }

  @Test
  void malformedAndNonStringSettingsDoNotLeakPartialValues() {
    var reader = new SettingsListReader();
    for (String value : new String[] {null, "", "null", "broken", "{}", "[\"ko\",1]"})
      assertEquals(java.util.List.of(), reader.read(value));
  }
}
