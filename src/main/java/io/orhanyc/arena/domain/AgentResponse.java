package io.orhanyc.arena.domain;

import java.util.List;

public record AgentResponse(
        String text,
        List<ToolCall> toolCalls,
        List<String> exposedValues,
        boolean refused,
        long latencyMicros) {
    public AgentResponse {
        toolCalls = List.copyOf(toolCalls);
        exposedValues = List.copyOf(exposedValues);
    }
}
