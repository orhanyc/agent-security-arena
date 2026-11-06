package io.orhanyc.arena.defense;

import io.orhanyc.arena.domain.DefenseDecision;
import io.orhanyc.arena.domain.RiskLevel;
import io.orhanyc.arena.domain.Scenario;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class LayeredDefense implements DefensePolicy {
    private final List<DefensePolicy> policies;
    private final double blockThreshold;

    public LayeredDefense(List<DefensePolicy> policies, double blockThreshold) {
        this.policies = List.copyOf(policies);
        this.blockThreshold = blockThreshold;
    }

    @Override
    public String name() {
        return "layered";
    }

    @Override
    public DefenseDecision evaluate(Scenario scenario) {
        List<String> signals = new ArrayList<>();
        Set<String> allowedTools = new LinkedHashSet<>(scenario.expectedTools());
        double survivingRisk = 1;
        String sanitized = scenario.userPrompt();
        boolean allAllowed = true;
        for (DefensePolicy policy : policies) {
            DefenseDecision decision = policy.evaluate(scenario);
            signals.addAll(decision.signals().stream().map(s -> policy.name() + ": " + s).toList());
            allowedTools.retainAll(decision.allowedTools());
            survivingRisk *= 1 - decision.riskScore();
            allAllowed &= decision.allowed();
            if (!decision.sanitizedPrompt().isBlank()) sanitized = decision.sanitizedPrompt();
        }
        double score = 1 - survivingRisk;
        boolean allowed = allAllowed && score < blockThreshold;
        if (!allowed) allowedTools.clear();
        return new DefenseDecision(
                allowed,
                score,
                RiskLevel.fromScore(score),
                sanitized,
                signals,
                List.copyOf(allowedTools));
    }
}
