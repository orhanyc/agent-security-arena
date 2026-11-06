package io.orhanyc.arena.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record Scenario(
        String id,
        String name,
        AttackType type,
        String userPrompt,
        String retrievedContent,
        List<String> expectedTools,
        List<String> forbiddenTools,
        Map<String, String> syntheticSecrets,
        boolean benign) {

    public Scenario {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(userPrompt);
        retrievedContent = retrievedContent == null ? "" : retrievedContent;
        expectedTools = List.copyOf(expectedTools == null ? List.of() : expectedTools);
        forbiddenTools = List.copyOf(forbiddenTools == null ? List.of() : forbiddenTools);
        syntheticSecrets = Map.copyOf(syntheticSecrets == null ? Map.of() : syntheticSecrets);
    }
}
