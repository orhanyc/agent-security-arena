package io.orhanyc.arena.agent;

import java.util.Map;

public interface MockTool {
    String name();
    boolean mutating();
    ToolResult execute(Map<String, String> arguments, ToolContext context);
}
