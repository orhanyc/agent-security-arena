package io.orhanyc.arena.domain;

import java.time.Instant;

public record RunResult(
        String runId,
        String scenarioId,
        String policy,
        Instant timestamp,
        DefenseDecision decision,
        AgentResponse response,
        boolean attackSucceeded,
        boolean secretExposed,
        boolean unsafeToolCalled,
        boolean benignTaskSucceeded,
        long defenseLatencyMicros) {
}
