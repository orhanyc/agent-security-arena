package io.orhanyc.arena.domain;

import java.util.List;

public record DefenseDecision(
        boolean allowed,
        double riskScore,
        RiskLevel riskLevel,
        String sanitizedPrompt,
        List<String> signals,
        List<String> allowedTools) {
    public DefenseDecision {
        signals = List.copyOf(signals);
        allowedTools = List.copyOf(allowedTools);
    }
}
