package io.orhanyc.arena;

import io.orhanyc.arena.agent.ReferenceAgent;
import io.orhanyc.arena.agent.SyntheticTools;
import io.orhanyc.arena.api.ArenaHttpServer;
import io.orhanyc.arena.api.Json;
import io.orhanyc.arena.core.ArenaRunner;
import io.orhanyc.arena.core.RunStore;
import io.orhanyc.arena.core.ScenarioCatalog;
import io.orhanyc.arena.defense.LayeredDefense;
import io.orhanyc.arena.defense.PatternDefense;
import io.orhanyc.arena.defense.ToolScopeDefense;
import io.orhanyc.arena.report.ArenaReport;

import java.util.List;
import java.util.Set;

public final class ArenaApplication {
    public static void main(String[] args) throws Exception {
        ScenarioCatalog catalog = ScenarioCatalog.defaults();
        PatternDefense pattern = new PatternDefense(0.55);
        ToolScopeDefense scope = new ToolScopeDefense(Set.of("search_docs", "read_profile"));
        LayeredDefense layered = new LayeredDefense(List.of(pattern, scope), 0.55);
        RunStore store = new RunStore(envInt("ARENA_MAX_EVENTS", 5000));
        ArenaRunner runner = new ArenaRunner(catalog, List.of(pattern, scope, layered), new ReferenceAgent(SyntheticTools.defaults()), store);

        if (args.length > 0 && args[0].equals("serve")) {
            int port = envInt("ARENA_PORT", 8080);
            ArenaHttpServer server = new ArenaHttpServer(port, catalog, runner, store);
            server.start();
            System.out.println("Agent Security Arena listening on http://localhost:" + port);
            return;
        }

        var results = runner.runAll("layered");
        System.out.println(Json.report(ArenaReport.from(results)));
    }

    private static int envInt(String name, int fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
    }
}
