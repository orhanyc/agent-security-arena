package io.orhanyc.arena.agent;

import java.util.Map;
import java.util.Set;

public record ToolContext(Map<String, String> syntheticSecrets, Set<String> grantedScopes) {
    public ToolContext {
        syntheticSecrets = Map.copyOf(syntheticSecrets);
        grantedScopes = Set.copyOf(grantedScopes);
    }
}
