/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.GraphWalk;

public abstract class Cycles {
    public static <V, E> GraphPath<V, E> simpleCycleToGraphPath(Graph<V, E> graph, List<E> cycle) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(cycle, "Cycle cannot be null");
        if (cycle.isEmpty()) {
            return null;
        }
        HashMap<V, E> firstEdge = new HashMap<V, E>();
        HashMap<V, E> secondEdge = new HashMap<V, E>();
        for (E e : cycle) {
            V s = graph.getEdgeSource(e);
            if (!firstEdge.containsKey(s)) {
                firstEdge.put(s, e);
            } else if (!secondEdge.containsKey(s)) {
                secondEdge.put(s, e);
            } else {
                throw new IllegalArgumentException("Not a simple cycle");
            }
            V t = graph.getEdgeTarget(e);
            if (!firstEdge.containsKey(t)) {
                firstEdge.put(t, e);
                continue;
            }
            if (!secondEdge.containsKey(t)) {
                secondEdge.put(t, e);
                continue;
            }
            throw new IllegalArgumentException("Not a simple cycle");
        }
        ArrayList edges = new ArrayList();
        double weight = 0.0;
        Object e = cycle.stream().findAny().get();
        edges.add(e);
        weight += graph.getEdgeWeight(e);
        V start = graph.getEdgeSource(e);
        V cur = Graphs.getOppositeVertex(graph, e, start);
        while (!cur.equals(start)) {
            Object fe = firstEdge.get(cur);
            if (fe == null) {
                throw new IllegalArgumentException("Not a simple cycle");
            }
            Object se = secondEdge.get(cur);
            if (se == null) {
                throw new IllegalArgumentException("Not a simple cycle");
            }
            if (fe.equals(e)) {
                e = se;
            } else if (se.equals(e)) {
                e = fe;
            } else {
                throw new IllegalArgumentException("Not a simple cycle");
            }
            edges.add(e);
            weight += graph.getEdgeWeight(e);
            cur = Graphs.getOppositeVertex(graph, e, cur);
        }
        return new GraphWalk<V, E>(graph, start, start, edges, weight);
    }
}

