/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.LexBreadthFirstIterator;
import org.jgrapht.traverse.MaximumCardinalityIterator;
import org.jgrapht.util.CollectionUtil;

public class ChordalityInspector<V, E> {
    private final IterationOrder iterationOrder;
    private final GraphIterator<V, E> orderIterator;
    private final Graph<V, E> graph;
    private boolean chordal = false;
    private List<V> order;
    private GraphPath<V, E> hole;

    public ChordalityInspector(Graph<V, E> graph) {
        this(graph, IterationOrder.MCS);
    }

    public ChordalityInspector(Graph<V, E> graph, IterationOrder iterationOrder) {
        Objects.requireNonNull(graph);
        this.graph = graph.getType().isDirected() ? new AsUndirectedGraph<V, E>(graph) : graph;
        this.iterationOrder = iterationOrder;
        this.hole = null;
        this.orderIterator = iterationOrder == IterationOrder.MCS ? new MaximumCardinalityIterator<V, E>(graph) : new LexBreadthFirstIterator<V, E>(graph);
    }

    public boolean isChordal() {
        if (this.order == null) {
            this.order = Collections.unmodifiableList(this.lazyComputeOrder());
            this.chordal = this.isPerfectEliminationOrder(this.order, true);
        }
        return this.chordal;
    }

    public List<V> getPerfectEliminationOrder() {
        this.isChordal();
        if (this.chordal) {
            return this.order;
        }
        return null;
    }

    public GraphPath<V, E> getHole() {
        this.isChordal();
        return this.hole;
    }

    public boolean isPerfectEliminationOrder(List<V> vertexOrder) {
        return this.isPerfectEliminationOrder(vertexOrder, false);
    }

    private List<V> lazyComputeOrder() {
        if (this.order == null) {
            int vertexNum = this.graph.vertexSet().size();
            this.order = new ArrayList<V>(vertexNum);
            for (int i = 0; i < vertexNum; ++i) {
                this.order.add(this.orderIterator.next());
            }
        }
        return this.order;
    }

    private boolean isPerfectEliminationOrder(List<V> vertexOrder, boolean computeHole) {
        Set<V> graphVertices = this.graph.vertexSet();
        if (graphVertices.size() == vertexOrder.size() && graphVertices.containsAll(vertexOrder)) {
            Map<V, Integer> vertexInOrder = this.getVertexInOrder(vertexOrder);
            for (V vertex : vertexOrder) {
                Set<V> predecessors = this.getPredecessors(vertexInOrder, vertex);
                if (predecessors.size() <= 0) continue;
                Object maxPredecessor = Collections.max(predecessors, Comparator.comparingInt(vertexInOrder::get));
                for (V predecessor : predecessors) {
                    if (predecessor.equals(maxPredecessor) || this.graph.containsEdge(predecessor, maxPredecessor)) continue;
                    if (computeHole) {
                        this.findHole(predecessor, vertex, maxPredecessor);
                    }
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Map<V, Integer> getVertexInOrder(List<V> vertexOrder) {
        HashMap<V, Integer> vertexInOrder = CollectionUtil.newHashMapWithExpectedSize(vertexOrder.size());
        int i = 0;
        for (V vertex : vertexOrder) {
            vertexInOrder.put(vertex, i++);
        }
        return vertexInOrder;
    }

    private void findHole(V a, V b, V c) {
        List<Object> cycle = new ArrayList<Object>(Arrays.asList(a, b, c));
        HashMap<V, Boolean> visited = CollectionUtil.newHashMapWithExpectedSize(this.graph.vertexSet().size());
        for (V vertex : this.graph.vertexSet()) {
            visited.put(vertex, false);
        }
        visited.put(a, true);
        visited.put(b, true);
        this.dfsVisit(cycle, visited, a, b, c);
        cycle = this.minimizeCycle(cycle);
        this.hole = new GraphWalk<Object, E>(this.graph, cycle, 0.0);
    }

    private void dfsVisit(List<V> cycle, Map<V, Boolean> visited, V finish, V middle, V current) {
        visited.put((Boolean)current, true);
        for (E edge : this.graph.edgesOf(current)) {
            V opposite = Graphs.getOppositeVertex(this.graph, edge, current);
            if ((visited.get(opposite).booleanValue() || this.graph.containsEdge(opposite, middle)) && !opposite.equals(finish)) continue;
            cycle.add(opposite);
            if (opposite.equals(finish)) {
                return;
            }
            this.dfsVisit(cycle, visited, finish, middle, opposite);
            if (cycle.get(cycle.size() - 1).equals(finish)) {
                return;
            }
            cycle.remove(cycle.size() - 1);
        }
    }

    private List<V> minimizeCycle(List<V> cycle) {
        HashSet<V> cycleVertices = new HashSet<V>(cycle);
        cycleVertices.remove(cycle.get(1));
        ArrayList<V> minimized = new ArrayList<V>();
        minimized.add(cycle.get(0));
        minimized.add(cycle.get(1));
        int i = 2;
        while (i < cycle.size() - 1) {
            V vertex = cycle.get(i);
            minimized.add(vertex);
            cycleVertices.remove(vertex);
            HashSet<V> forward = new HashSet<V>();
            for (E edge : this.graph.edgesOf(vertex)) {
                V opposite = Graphs.getOppositeVertex(this.graph, edge, vertex);
                if (!cycleVertices.contains(opposite)) continue;
                forward.add(opposite);
            }
            for (E forwardVertex : forward) {
                if (!cycleVertices.contains(forwardVertex)) continue;
                do {
                    cycleVertices.remove(cycle.get(i));
                } while (++i < cycle.size() && !cycle.get(i).equals(forwardVertex));
            }
        }
        minimized.add(cycle.get(cycle.size() - 1));
        return minimized;
    }

    private Set<V> getPredecessors(Map<V, Integer> vertexInOrder, V vertex) {
        HashSet<V> predecessors = new HashSet<V>();
        Integer vertexPosition = vertexInOrder.get(vertex);
        Set<E> edges = this.graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(this.graph, edge, vertex);
            Integer destPosition = vertexInOrder.get(oppositeVertex);
            if (destPosition >= vertexPosition) continue;
            predecessors.add(oppositeVertex);
        }
        return predecessors;
    }

    public IterationOrder getIterationOrder() {
        return this.iterationOrder;
    }

    public static enum IterationOrder {
        MCS,
        LEX_BFS;

    }
}

