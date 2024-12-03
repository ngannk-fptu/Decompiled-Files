/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.providers.DirectedGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycleDetector<T> {
    private DirectedGraph<T> graph;
    private Map<T, Status> marks;
    private List<T> verticesInCycles;

    public CycleDetector(DirectedGraph<T> graph) {
        this.graph = graph;
        this.marks = new HashMap<T, Status>();
        this.verticesInCycles = new ArrayList<T>();
    }

    public boolean containsCycle() {
        for (T v : this.graph) {
            if (!this.marks.containsKey(v) && !this.mark(v)) continue;
        }
        return !this.verticesInCycles.isEmpty();
    }

    private boolean mark(T vertex) {
        ArrayList<T> localCycles = new ArrayList<T>();
        this.marks.put(vertex, Status.MARKED);
        for (T u : this.graph.edgesFrom(vertex)) {
            if (this.marks.get(u) == Status.MARKED) {
                localCycles.add(vertex);
                continue;
            }
            if (this.marks.containsKey(u) || !this.mark(u)) continue;
            localCycles.add(vertex);
        }
        this.marks.put(vertex, Status.COMPLETE);
        this.verticesInCycles.addAll(localCycles);
        return !localCycles.isEmpty();
    }

    public List<T> getVerticesInCycles() {
        return this.verticesInCycles;
    }

    private static enum Status {
        MARKED,
        COMPLETE;

    }
}

