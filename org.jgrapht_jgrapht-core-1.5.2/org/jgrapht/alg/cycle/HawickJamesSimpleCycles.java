/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;

public class HawickJamesSimpleCycles<V, E>
implements DirectedSimpleCycles<V, E> {
    private Graph<V, E> graph;
    private int nVertices = 0;
    private long nCycles = 0L;
    private Integer start = 0;
    private List<Integer>[] aK = null;
    private List<Integer>[] b = null;
    private boolean[] blocked = null;
    private ArrayDeque<Integer> stack = null;
    private V[] iToV = null;
    private Map<V, Integer> vToI = null;
    private int pathLimit = 0;
    private boolean hasLimit = false;
    private Runnable operation;

    public HawickJamesSimpleCycles() {
    }

    public HawickJamesSimpleCycles(Graph<V, E> graph) throws IllegalArgumentException {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    private void initState() {
        int i;
        this.nCycles = 0L;
        this.nVertices = this.graph.vertexSet().size();
        this.blocked = new boolean[this.nVertices];
        this.stack = new ArrayDeque(this.nVertices);
        this.b = new ArrayList[this.nVertices];
        for (i = 0; i < this.nVertices; ++i) {
            this.b[i] = new ArrayList<Integer>();
        }
        this.iToV = this.graph.vertexSet().toArray();
        this.vToI = new HashMap<V, Integer>();
        for (i = 0; i < this.iToV.length; ++i) {
            this.vToI.put((Integer)this.iToV[i], i);
        }
        this.aK = this.buildAdjacencyList();
        this.stack.clear();
    }

    private List<Integer>[] buildAdjacencyList() {
        ArrayList[] listAk = new ArrayList[this.nVertices];
        for (int j = 0; j < this.nVertices; ++j) {
            V v = this.iToV[j];
            List<V> s = Graphs.successorListOf(this.graph, v);
            listAk[j] = new ArrayList(s.size());
            for (V value : s) {
                listAk[j].add(this.vToI.get(value));
            }
        }
        return listAk;
    }

    private void clearState() {
        this.aK = null;
        this.nVertices = 0;
        this.blocked = null;
        this.stack = null;
        this.iToV = null;
        this.vToI = null;
        this.b = null;
        this.operation = () -> {};
    }

    private boolean circuit(Integer v, int steps) {
        boolean f = false;
        this.stack.push(v);
        this.blocked[v.intValue()] = true;
        for (Integer w : this.aK[v]) {
            if (w < this.start) continue;
            if (Objects.equals(w, this.start)) {
                this.operation.run();
                f = true;
                continue;
            }
            if (this.blocked[w] || !this.limitReached(steps) && !this.circuit(w, steps + 1)) continue;
            f = true;
        }
        if (f) {
            this.unblock(v);
        } else {
            for (Integer w : this.aK[v]) {
                if (w < this.start || this.b[w].contains(v)) continue;
                this.b[w].add(v);
            }
        }
        this.stack.pop();
        return f;
    }

    private void unblock(Integer u) {
        this.blocked[u.intValue()] = false;
        for (int wPos = 0; wPos < this.b[u].size(); ++wPos) {
            Integer w = this.b[u].get(wPos);
            int sizeBeforeRemove = this.b[u].size();
            this.b[u].removeAll(Collections.singletonList(w));
            wPos -= sizeBeforeRemove - this.b[u].size();
            if (!this.blocked[w]) continue;
            this.unblock(w);
        }
    }

    public Graph<V, E> getGraph() {
        return this.graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    @Override
    public void findSimpleCycles(Consumer<List<V>> consumer) throws IllegalArgumentException {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        this.initState();
        this.operation = () -> {
            List cycle = this.stack.stream().map(v -> this.iToV[v]).collect(Collectors.toList());
            Collections.reverse(cycle);
            consumer.accept(cycle);
        };
        this.analyzeCircuits();
        this.clearState();
    }

    public void printSimpleCycles() {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        this.initState();
        this.operation = () -> {
            this.stack.stream().map(i -> this.iToV[i].toString() + " ").forEach(System.out::print);
            System.out.println();
        };
        this.analyzeCircuits();
        this.clearState();
    }

    public long countSimpleCycles() {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        this.initState();
        this.nCycles = 0L;
        this.operation = () -> ++this.nCycles;
        this.analyzeCircuits();
        this.clearState();
        return this.nCycles;
    }

    private void analyzeCircuits() {
        for (int i = 0; i < this.nVertices; ++i) {
            for (int j = 0; j < this.nVertices; ++j) {
                this.blocked[j] = false;
                this.b[j].clear();
            }
            this.start = this.vToI.get(this.iToV[i]);
            this.circuit(this.start, 0);
        }
    }

    public void setPathLimit(int pathLimit) {
        this.pathLimit = pathLimit - 1;
        this.hasLimit = true;
    }

    public void clearPathLimit() {
        this.hasLimit = false;
    }

    private boolean limitReached(int steps) {
        return this.hasLimit && steps >= this.pathLimit;
    }
}

