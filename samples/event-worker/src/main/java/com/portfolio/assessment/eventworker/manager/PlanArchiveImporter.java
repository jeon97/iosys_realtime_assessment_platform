package com.portfolio.assessment.eventworker.manager;

import java.util.Map;
import java.util.Set;

public final class PlanArchiveImporter {
    private static final Set<String> REQUIRED_FILES = Set.of(
            "plan.json", "package.json", "users.json"
    );

    private final PlanDocumentDecoder decoder;
    private final PlanBundleGateway gateway;

    public PlanArchiveImporter(PlanDocumentDecoder decoder, PlanBundleGateway gateway) {
        this.decoder = decoder;
        this.gateway = gateway;
    }

    public PlanBundle importArchive(Map<String, String> archiveEntries) {
        archiveEntries.keySet().forEach(this::validateEntryName);
        if (!archiveEntries.keySet().containsAll(REQUIRED_FILES)) {
            throw new IllegalArgumentException("required plan documents are missing");
        }

        PlanBundle bundle = new PlanBundle(
                decoder.decodeObject(archiveEntries.get("plan.json")),
                decoder.decodeObject(archiveEntries.get("package.json")),
                decoder.decodeList(archiveEntries.get("users.json"))
        );
        gateway.saveAll(bundle);
        return bundle;
    }

    private void validateEntryName(String entryName) {
        if (entryName.startsWith("/") || entryName.contains("..") || entryName.contains("\\")) {
            throw new IllegalArgumentException("unsafe archive entry: " + entryName);
        }
        if (!entryName.endsWith(".json")) {
            throw new IllegalArgumentException("only JSON documents are accepted");
        }
    }
}

