package io.orhanyc.arena.defense;

import io.orhanyc.arena.domain.DefenseDecision;
import io.orhanyc.arena.domain.RiskLevel;
import io.orhanyc.arena.domain.Scenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ToolScopeDefense implements DefensePolicy {
    private final Set<String> readOnlyTools;

    public ToolScopeDefense(Set<String> readOnlyTools) {
        this.readOnlyTools = Set.copyOf(readOnlyTools);
    }

    @Override
    public String name() {
        return "tool-scope";
    }

    @Override
    public DefenseDecision evaluate(Scenario scenario) {
        Set<String> requested = new HashSet<>(scenario.expectedTools());
        List<String> allowed = requested.stream().filter(readOnlyTools::contains).sorted().toList();
        List<String> blocked = requested.stream().filter(tool -> !readOnlyTools.contains(tool)).sorted().toList();
        List<String> signals = new ArrayList<>();
        if (!blocked.isEmpty()) signals.add("tools outside read-only scope: " + String.join(",", blocked));
        if (!scenario.forbiddenTools().isEmpty()) signals.add("scenario includes forbidden tool targets");
        double score = Math.min(1, blocked.size() * 0.35 + scenario.forbiddenTools().size() * 0.25);
        return new DefenseDecision(
                blocked.isEmpty(),
                score,
                RiskLevel.fromScore(score),
                scenario.userPrompt(),
                signals,
                allowed);
    }
}
