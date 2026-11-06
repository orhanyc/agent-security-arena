package io.orhanyc.arena.agent;

import io.orhanyc.arena.domain.AgentResponse;
import io.orhanyc.arena.domain.DefenseDecision;
import io.orhanyc.arena.domain.Scenario;
import io.orhanyc.arena.domain.ToolCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ReferenceAgent {
    private final Map<String, MockTool> tools;

    public ReferenceAgent(Map<String, MockTool> tools) {
        this.tools = Map.copyOf(tools);
    }

    public AgentResponse run(Scenario scenario, DefenseDecision decision) {
        long started = System.nanoTime();
        if (!decision.allowed()) {
            return new AgentResponse("Request blocked by defense policy", List.of(), List.of(), true, elapsed(started));
        }
        List<ToolCall> calls = new ArrayList<>();
        List<String> exposed = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        for (String toolName : inferTools(scenario, decision)) {
            MockTool tool = tools.get(toolName);
            if (tool == null) continue;
            boolean authorized = decision.allowedTools().contains(toolName);
            calls.add(new ToolCall(toolName, argumentsFor(toolName, scenario), authorized));
            if (!authorized) {
                outputs.add("denied " + toolName);
                continue;
            }
            ToolContext context = new ToolContext(scenario.syntheticSecrets(), scopesFor(scenario));
            ToolResult result = tool.execute(argumentsFor(toolName, scenario), context);
            outputs.add(result.output());
            exposed.addAll(result.exposedValues());
        }
        String text = outputs.isEmpty() ? "Synthetic task completed without tools" : String.join("; ", outputs);
        return new AgentResponse(text, calls, exposed, false, elapsed(started));
    }

    private List<String> inferTools(Scenario scenario, DefenseDecision decision) {
        List<String> inferred = new ArrayList<>(scenario.expectedTools());
        String content = (scenario.userPrompt() + " " + scenario.retrievedContent()).toLowerCase(Locale.ROOT);
        if (content.contains("reveal") || content.contains("secret")) inferred.add("read_secret");
        if (content.contains("delete")) inferred.add("delete_records");
        if (content.contains("export") || content.contains("send data")) inferred.add("export_data");
        return inferred.stream().distinct().toList();
    }

    private Map<String, String> argumentsFor(String tool, Scenario scenario) {
        return switch (tool) {
            case "search_docs" -> Map.of("query", scenario.name());
            case "read_secret" -> Map.of("key", scenario.syntheticSecrets().keySet().stream().findFirst().orElse("demo_token"));
            case "export_data" -> Map.of("destination", "synthetic://blocked-sink");
            case "delete_records" -> Map.of("filter", "synthetic=true");
            default -> Map.of();
        };
    }

    private Set<String> scopesFor(Scenario scenario) {
        if (scenario.benign()) return Set.of("profile:read", "docs:search");
        return Set.of("profile:read", "docs:search");
    }

    private long elapsed(long started) {
        return (System.nanoTime() - started) / 1_000;
    }
}
