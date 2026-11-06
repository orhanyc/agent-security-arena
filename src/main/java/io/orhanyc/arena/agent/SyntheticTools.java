package io.orhanyc.arena.agent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SyntheticTools {
    private SyntheticTools() {}

    public static Map<String, MockTool> defaults() {
        Map<String, MockTool> tools = new LinkedHashMap<>();
        register(tools, new SearchTool());
        register(tools, new ProfileTool());
        register(tools, new ExportTool());
        register(tools, new DeleteTool());
        register(tools, new SecretTool());
        return Map.copyOf(tools);
    }

    private static void register(Map<String, MockTool> tools, MockTool tool) {
        tools.put(tool.name(), tool);
    }

    private static final class SearchTool implements MockTool {
        public String name() { return "search_docs"; }
        public boolean mutating() { return false; }
        public ToolResult execute(Map<String, String> arguments, ToolContext context) {
            String query = arguments.getOrDefault("query", "general");
            return new ToolResult(true, "Synthetic results for " + query, List.of());
        }
    }

    private static final class ProfileTool implements MockTool {
        public String name() { return "read_profile"; }
        public boolean mutating() { return false; }
        public ToolResult execute(Map<String, String> arguments, ToolContext context) {
            return new ToolResult(true, "Synthetic profile: demo-user", List.of());
        }
    }

    private static final class ExportTool implements MockTool {
        public String name() { return "export_data"; }
        public boolean mutating() { return true; }
        public ToolResult execute(Map<String, String> arguments, ToolContext context) {
            if (!context.grantedScopes().contains("data:export")) return ToolResult.denied("missing data:export scope");
            return new ToolResult(true, "Synthetic export created", List.of());
        }
    }

    private static final class DeleteTool implements MockTool {
        public String name() { return "delete_records"; }
        public boolean mutating() { return true; }
        public ToolResult execute(Map<String, String> arguments, ToolContext context) {
            if (!context.grantedScopes().contains("records:delete")) return ToolResult.denied("missing records:delete scope");
            return new ToolResult(true, "Synthetic records deleted", List.of());
        }
    }

    private static final class SecretTool implements MockTool {
        public String name() { return "read_secret"; }
        public boolean mutating() { return false; }
        public ToolResult execute(Map<String, String> arguments, ToolContext context) {
            if (!context.grantedScopes().contains("secrets:read")) return ToolResult.denied("missing secrets:read scope");
            String key = arguments.getOrDefault("key", "demo_token");
            String value = context.syntheticSecrets().getOrDefault(key, "missing");
            return new ToolResult(true, value, List.of(value));
        }
    }
}
