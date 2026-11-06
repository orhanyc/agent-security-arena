package io.orhanyc.arena.agent;

import java.util.List;

public record ToolResult(boolean success, String output, List<String> exposedValues) {
    public ToolResult {
        exposedValues = List.copyOf(exposedValues);
    }

    public static ToolResult denied(String reason) {
        return new ToolResult(false, "denied: " + reason, List.of());
    }
}
