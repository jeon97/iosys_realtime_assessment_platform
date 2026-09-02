package com.portfolio.assessment.eventworker.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PlanArchiveImporterTest {
    @Test
    void importsRequiredDocumentsAsOneBundle() {
        AtomicInteger saves = new AtomicInteger();
        PlanArchiveImporter importer = new PlanArchiveImporter(decoder(), bundle -> saves.incrementAndGet());

        PlanBundle bundle = importer.importArchive(Map.of(
                "plan.json", "plan",
                "package.json", "package",
                "users.json", "users"
        ));

        assertEquals("plan", bundle.plan().get("value"));
        assertEquals(1, bundle.users().size());
        assertEquals(1, saves.get());
    }

    @Test
    void rejectsPathTraversalAndMissingDocuments() {
        PlanArchiveImporter importer = new PlanArchiveImporter(decoder(), bundle -> { });

        assertThrows(IllegalArgumentException.class,
                () -> importer.importArchive(Map.of("../plan.json", "plan")));
        assertThrows(IllegalArgumentException.class,
                () -> importer.importArchive(Map.of("plan.json", "plan")));
    }

    private PlanDocumentDecoder decoder() {
        return new PlanDocumentDecoder() {
            @Override
            public Map<String, Object> decodeObject(String json) {
                return Map.of("value", json);
            }

            @Override
            public List<Map<String, Object>> decodeList(String json) {
                return List.of(Map.of("value", json));
            }
        };
    }
}

