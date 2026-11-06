package io.orhanyc.arena.api;

import io.orhanyc.arena.domain.RunResult;
import io.orhanyc.arena.domain.Scenario;
import io.orhanyc.arena.report.ArenaReport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Json {
    private Json() {}

    public static String scenario(Scenario value) {
        return object(Map.of(
                "id", quote(value.id()),
                "name", quote(value.name()),
                "type", quote(value.type().name()),
                "benign", Boolean.toString(value.benign()),
                "expectedTools", strings(value.expectedTools()),
                "forbiddenTools", strings(value.forbiddenTools())));
    }

    public static String run(RunResult value) {
        return object(Map.ofEntries(
                Map.entry("runId", quote(value.runId())),
                Map.entry("scenarioId", quote(value.scenarioId())),
                Map.entry("policy", quote(value.policy())),
                Map.entry("timestamp", quote(value.timestamp().toString())),
                Map.entry("riskScore", number(value.decision().riskScore())),
                Map.entry("riskLevel", quote(value.decision().riskLevel().name())),
                Map.entry("allowed", Boolean.toString(value.decision().allowed())),
                Map.entry("signals", strings(value.decision().signals())),
                Map.entry("attackSucceeded", Boolean.toString(value.attackSucceeded())),
                Map.entry("secretExposed", Boolean.toString(value.secretExposed())),
                Map.entry("unsafeToolCalled", Boolean.toString(value.unsafeToolCalled())),
                Map.entry("benignTaskSucceeded", Boolean.toString(value.benignTaskSucceeded())),
                Map.entry("defenseLatencyMicros", Long.toString(value.defenseLatencyMicros()))));
    }

    public static String report(ArenaReport value) {
        return object(Map.ofEntries(
                Map.entry("totalRuns", Integer.toString(value.totalRuns())),
                Map.entry("attackRuns", Integer.toString(value.attackRuns())),
                Map.entry("benignRuns", Integer.toString(value.benignRuns())),
                Map.entry("attackSuccessRate", number(value.attackSuccessRate())),
                Map.entry("secretExfiltrationRate", number(value.secretExfiltrationRate())),
                Map.entry("unsafeToolCallRate", number(value.unsafeToolCallRate())),
                Map.entry("benignTaskSuccessRate", number(value.benignTaskSuccessRate())),
                Map.entry("falseBlockRate", number(value.falseBlockRate())),
                Map.entry("averageDefenseLatencyMicros", number(value.averageDefenseLatencyMicros())),
                Map.entry("riskLevels", stringLongMap(value.riskLevels()))));
    }

    public static String array(List<String> values) {
        return "[" + String.join(",", values) + "]";
    }

    public static String strings(List<String> values) {
        return array(values.stream().map(Json::quote).toList());
    }

    public static String object(Map<String, String> fields) {
        return "{" + fields.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> quote(e.getKey()) + ":" + e.getValue()).collect(Collectors.joining(",")) + "}";
    }

    public static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    private static String number(double value) {
        return String.format(java.util.Locale.ROOT, "%.6f", value);
    }

    private static String stringLongMap(Map<String, Long> values) {
        return object(values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Long.toString(e.getValue()))));
    }
}
