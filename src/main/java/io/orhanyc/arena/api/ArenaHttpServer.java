package io.orhanyc.arena.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.orhanyc.arena.core.ArenaRunner;
import io.orhanyc.arena.core.RunStore;
import io.orhanyc.arena.core.ScenarioCatalog;
import io.orhanyc.arena.report.ArenaReport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public final class ArenaHttpServer {
    private final HttpServer server;
    private final ScenarioCatalog catalog;
    private final ArenaRunner runner;
    private final RunStore store;

    public ArenaHttpServer(int port, ScenarioCatalog catalog, ArenaRunner runner, RunStore store) throws IOException {
        this.catalog = catalog;
        this.runner = runner;
        this.store = store;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", exchange -> respond(exchange, 200, Json.object(Map.of("status", Json.quote("ok"), "runs", Integer.toString(store.size())))));
        server.createContext("/scenarios", this::scenarios);
        server.createContext("/policies", exchange -> respond(exchange, 200, Json.strings(runner.policyNames())));
        server.createContext("/runs", this::runs);
        server.createContext("/reports/latest", this::report);
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void scenarios(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            respond(exchange, 405, error("method not allowed"));
            return;
        }
        respond(exchange, 200, Json.array(catalog.all().stream().map(Json::scenario).toList()));
    }

    private void runs(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            respond(exchange, 200, Json.array(store.recent(50).stream().map(Json::run).toList()));
            return;
        }
        if (!"POST".equals(exchange.getRequestMethod())) {
            respond(exchange, 405, error("method not allowed"));
            return;
        }
        Map<String, String> query = query(exchange.getRequestURI().getRawQuery());
        String scenario = query.getOrDefault("scenario", "benign-search");
        String policy = query.getOrDefault("policy", "layered");
        try {
            respond(exchange, 201, Json.run(runner.run(scenario, policy)));
        } catch (IllegalArgumentException error) {
            respond(exchange, 400, error(error.getMessage()));
        }
    }

    private void report(HttpExchange exchange) throws IOException {
        List<io.orhanyc.arena.domain.RunResult> recent = store.recent(500);
        respond(exchange, 200, Json.report(ArenaReport.from(recent)));
    }

    private Map<String, String> query(String raw) {
        if (raw == null || raw.isBlank()) return Map.of();
        return java.util.Arrays.stream(raw.split("&")).map(part -> part.split("=", 2)).collect(java.util.stream.Collectors.toMap(parts -> decode(parts[0]), parts -> parts.length > 1 ? decode(parts[1]) : ""));
    }

    private String decode(String value) {
        return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String error(String message) {
        return Json.object(Map.of("error", Json.quote(message)));
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
