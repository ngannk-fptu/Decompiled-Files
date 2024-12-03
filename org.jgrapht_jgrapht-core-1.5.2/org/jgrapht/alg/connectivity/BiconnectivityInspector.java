/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.AsUndirectedGraph;

public class BiconnectivityInspector<V, E> {
    private Graph<V, E> graph;
    private Set<Graph<V, E>> blocks;
    private Set<V> cutpoints;
    private Set<E> bridges;
    private Set<V> connectedSet;
    private Set<Set<V>> connectedSets;
    private Set<Graph<V, E>> connectedComponents;
    private Map<V, Set<Graph<V, E>>> vertex2blocks;
    private Map<V, Graph<V, E>> vertex2components;
    private int time;
    private Deque<E> stack;
    private Map<V, Integer> discTime = new HashMap<V, Integer>();

    public BiconnectivityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<V, E>(graph);
        }
    }

    public Set<V> getCutpoints() {
        this.performLazyInspection();
        return this.cutpoints;
    }

    public Set<E> getBridges() {
        this.performLazyInspection();
        return this.bridges;
    }

    public Set<Graph<V, E>> getBlocks(V vertex) {
        assert (this.graph.containsVertex(vertex));
        if (this.vertex2blocks == null) {
            this.vertex2blocks = new HashMap<V, Set<Graph<V, E>>>();
            for (V v : this.graph.vertexSet()) {
                this.vertex2blocks.put(v, new LinkedHashSet());
            }
            for (Graph graph : this.getBlocks()) {
                for (Object v : graph.vertexSet()) {
                    this.vertex2blocks.get(v).add(graph);
                }
            }
        }
        return this.vertex2blocks.get(vertex);
    }

    public Set<Graph<V, E>> getBlocks() {
        this.performLazyInspection();
        return this.blocks;
    }

    public Set<Graph<V, E>> getConnectedComponents() {
        if (this.connectedComponents == null) {
            this.performLazyInspection();
            this.connectedComponents = new LinkedHashSet<Graph<V, E>>();
            for (Set<V> vertexComponent : this.connectedSets) {
                this.connectedComponents.add(new AsSubgraph<V, E>(this.graph, vertexComponent));
            }
        }
        return this.connectedComponents;
    }

    public Graph<V, E> getConnectedComponent(V vertex) {
        assert (this.graph.containsVertex(vertex));
        if (this.vertex2components == null) {
            this.vertex2components = new HashMap<V, Graph<V, E>>();
            for (Graph<V, E> component : this.getConnectedComponents()) {
                for (V v : component.vertexSet()) {
                    this.vertex2components.put((Graph<V, E>)v, (Graph<Graph<V, E>, E>)component);
                }
            }
        }
        return this.vertex2components.get(vertex);
    }

    public boolean isBiconnected() {
        this.performLazyInspection();
        return this.graph.vertexSet().size() >= 2 && this.blocks.size() == 1;
    }

    public boolean isConnected() {
        this.performLazyInspection();
        return this.connectedSets.size() == 1;
    }

    private void init() {
        this.blocks = new LinkedHashSet<Graph<V, E>>();
        this.cutpoints = new LinkedHashSet<V>();
        this.bridges = new LinkedHashSet();
        this.connectedSets = new LinkedHashSet<Set<V>>();
        this.stack = new ArrayDeque(this.graph.edgeSet().size());
        for (V v : this.graph.vertexSet()) {
            this.discTime.put((Integer)v, -1);
        }
    }

    private void performLazyInspection() {
        if (this.blocks == null) {
            this.init();
            for (V v : this.graph.vertexSet()) {
                if (this.discTime.get(v) != -1) continue;
                this.connectedSet = new HashSet<V>();
                this.dfs(v, null);
                if (!this.stack.isEmpty()) {
                    this.buildBlock(0);
                }
                this.connectedSets.add(this.connectedSet);
            }
            if (this.graph.getType().isAllowingMultipleEdges()) {
                Iterator<E> it = this.bridges.iterator();
                while (it.hasNext()) {
                    E edge = it.next();
                    int nrParallelEdges = this.graph.getAllEdges(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge)).size();
                    if (nrParallelEdges <= 1) continue;
                    it.remove();
                }
            }
        }
    }

    private void buildBlock(int discTimeCutpoint) {
        HashSet<V> vertexComponent = new HashSet<V>();
        while (!this.stack.isEmpty()) {
            E edge = this.stack.peek();
            V source = this.graph.getEdgeSource(edge);
            V target = this.graph.getEdgeTarget(edge);
            if (this.discTime.get(source) < discTimeCutpoint && this.discTime.get(target) < discTimeCutpoint) break;
            this.stack.pop();
            vertexComponent.add(source);
            vertexComponent.add(target);
        }
        this.blocks.add(new AsSubgraph<V, E>(this.graph, vertexComponent));
    }

    private int dfs(V v, V parent) {
        int lowV = ++this.time;
        this.discTime.put((Integer)v, this.time);
        this.connectedSet.add(v);
        int children = 0;
        for (E edge : this.graph.edgesOf(v)) {
            V nv = Graphs.getOppositeVertex(this.graph, edge, v);
            if (this.discTime.get(nv) == -1) {
                ++children;
                this.stack.push(edge);
                int lowNV = this.dfs(nv, v);
                lowV = Math.min(lowNV, lowV);
                if (lowNV > this.discTime.get(v)) {
                    this.bridges.add(edge);
                }
                if ((parent == null || lowNV < this.discTime.get(v)) && (parent != null || children <= 1)) continue;
                this.cutpoints.add(v);
                this.buildBlock(this.discTime.get(nv));
                continue;
            }
            if (this.discTime.get(nv) >= this.discTime.get(v) || nv.equals(parent)) continue;
            this.stack.push(edge);
            lowV = Math.min(this.discTime.get(nv), lowV);
        }
        return lowV;
    }
}

