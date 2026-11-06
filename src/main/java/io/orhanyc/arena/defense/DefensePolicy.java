package io.orhanyc.arena.defense;

import io.orhanyc.arena.domain.DefenseDecision;
import io.orhanyc.arena.domain.Scenario;

public interface DefensePolicy {
    String name();
    DefenseDecision evaluate(Scenario scenario);
}
