/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.FixedSizeIntegerQueue;

public class HopcroftKarpMaximumCardinalityBipartiteMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;
    private List<V> vertices;
    private Map<V, Integer> vertexIndexMap;
    private int matchedVertices;
    private static final int DUMMY = 0;
    private static final int INF = Integer.MAX_VALUE;
    private int[] matching;
    private int[] dist;
    private FixedSizeIntegerQueue queue;

    public HopcroftKarpMaximumCardinalityBipartiteMatching(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
        this.graph = GraphTests.requireUndirected(graph);
        if (partition1.size() <= partition2.size()) {
            this.partition1 = partition1;
            this.partition2 = partition2;
        } else {
            this.partition1 = partition2;
            this.partition2 = partition1;
        }
    }

    private void init() {
        this.vertices = new ArrayList<V>();
        this.vertices.add(null);
        this.vertices.addAll(this.partition1);
        this.vertices.addAll(this.partition2);
        this.vertexIndexMap = new HashMap<V, Integer>();
        for (int i = 0; i < this.vertices.size(); ++i) {
            this.vertexIndexMap.put((Integer)this.vertices.get(i), i);
        }
        this.matching = new int[this.vertices.size() + 1];
        this.dist = new int[this.partition1.size() + 1];
        this.queue = new FixedSizeIntegerQueue(this.vertices.size());
    }

    private void warmStart() {
        block0: for (V uOrig : this.partition1) {
            int u = this.vertexIndexMap.get(uOrig);
            for (V vOrig : Graphs.neighborListOf(this.graph, uOrig)) {
                int v = this.vertexIndexMap.get(vOrig);
                if (this.matching[v] != 0) continue;
                this.matching[v] = u;
                this.matching[u] = v;
                ++this.matchedVertices;
                continue block0;
            }
        }
    }

    private boolean bfs() {
        int u;
        this.queue.clear();
        for (u = 1; u <= this.partition1.size(); ++u) {
            if (this.matching[u] == 0) {
                this.dist[u] = 0;
                this.queue.enqueue(u);
                continue;
            }
            this.dist[u] = Integer.MAX_VALUE;
        }
        this.dist[0] = Integer.MAX_VALUE;
        while (!this.queue.isEmpty()) {
            u = this.queue.poll();
            if (this.dist[u] >= this.dist[0]) continue;
            for (V vOrig : Graphs.neighborListOf(this.graph, this.vertices.get(u))) {
                int v = this.vertexIndexMap.get(vOrig);
                if (this.dist[this.matching[v]] != Integer.MAX_VALUE) continue;
                this.dist[this.matching[v]] = this.dist[u] + 1;
                this.queue.enqueue(this.matching[v]);
            }
        }
        return this.dist[0] != Integer.MAX_VALUE;
    }

    private boolean dfs(int u) {
        if (u != 0) {
            for (V vOrig : Graphs.neighborListOf(this.graph, this.vertices.get(u))) {
                int v = this.vertexIndexMap.get(vOrig);
                if (this.dist[this.matching[v]] != this.dist[u] + 1 || !this.dfs(this.matching[v])) continue;
                this.matching[v] = u;
                this.matching[u] = v;
                return true;
            }
            this.dist[u] = Integer.MAX_VALUE;
            return false;
        }
        return true;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        this.init();
        this.warmStart();
        while (this.matchedVertices < this.partition1.size() && this.bfs()) {
            for (int v = 1; v <= this.partition1.size() && this.matchedVertices < this.partition1.size(); ++v) {
                if (this.matching[v] != 0 || !this.dfs(v)) continue;
                ++this.matchedVertices;
            }
        }
        assert (this.matchedVertices <= this.partition1.size());
        HashSet<E> edges = new HashSet<E>();
        for (int i = 0; i < this.vertices.size(); ++i) {
            if (this.matching[i] == 0) continue;
            edges.add(this.graph.getEdge(this.vertices.get(i), this.vertices.get(this.matching[i])));
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, edges, edges.size());
    }
}

