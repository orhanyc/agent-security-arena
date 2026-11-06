package io.orhanyc.arena.core;

import io.orhanyc.arena.domain.RunResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class RunStore {
    private final ConcurrentHashMap<String, RunResult> byId = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<String> order = new ConcurrentLinkedDeque<>();
    private final int capacity;

    public RunStore(int capacity) {
        this.capacity = capacity;
    }

    public void save(RunResult result) {
        byId.put(result.runId(), result);
        order.addFirst(result.runId());
        while (order.size() > capacity) {
            String removed = order.pollLast();
            if (removed != null) byId.remove(removed);
        }
    }

    public Optional<RunResult> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<RunResult> recent(int limit) {
        List<RunResult> results = new ArrayList<>();
        for (String id : order) {
            if (results.size() >= limit) break;
            RunResult result = byId.get(id);
            if (result != null) results.add(result);
        }
        return List.copyOf(results);
    }

    public int size() {
        return byId.size();
    }
}
