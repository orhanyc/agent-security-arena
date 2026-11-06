package io.orhanyc.arena.domain;

import java.util.Map;

public record ToolCall(String tool, Map<String, String> arguments, boolean authorized) {
    public ToolCall {
        arguments = Map.copyOf(arguments == null ? Map.of() : arguments);
    }
}
