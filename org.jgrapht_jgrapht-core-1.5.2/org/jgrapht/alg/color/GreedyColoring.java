/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

public class GreedyColoring<V, E>
implements VertexColoringAlgorithm<V> {
    protected static final String SELF_LOOPS_NOT_ALLOWED = "Self-loops not allowed";
    protected final Graph<V, E> graph;

    public GreedyColoring(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    protected Iterable<V> getVertexOrdering() {
        return this.graph.vertexSet();
    }

    @Override
    public VertexColoringAlgorithm.Coloring<V> getColoring() {
        int maxColor = -1;
        HashMap<V, Integer> colors = new HashMap<V, Integer>();
        HashSet<Integer> used = new HashSet<Integer>();
        for (V v : this.getVertexOrdering()) {
            for (E e : this.graph.edgesOf(v)) {
                V u = Graphs.getOppositeVertex(this.graph, e, v);
                if (v.equals(u)) {
                    throw new IllegalArgumentException(SELF_LOOPS_NOT_ALLOWED);
                }
                if (!colors.containsKey(u)) continue;
                used.add((Integer)colors.get(u));
            }
            int candidate = 0;
            while (used.contains(candidate)) {
                ++candidate;
            }
            used.clear();
            colors.put(v, candidate);
            maxColor = Math.max(maxColor, candidate);
        }
        return new VertexColoringAlgorithm.ColoringImpl(colors, maxColor + 1);
    }
}

