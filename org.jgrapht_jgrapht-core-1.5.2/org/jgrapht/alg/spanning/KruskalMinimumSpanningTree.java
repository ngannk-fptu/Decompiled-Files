/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.spanning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.UnionFind;

public class KruskalMinimumSpanningTree<V, E>
implements SpanningTreeAlgorithm<E> {
    private final Graph<V, E> graph;

    public KruskalMinimumSpanningTree(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    @Override
    public SpanningTreeAlgorithm.SpanningTree<E> getSpanningTree() {
        UnionFind<V> forest = new UnionFind<V>(this.graph.vertexSet());
        ArrayList<Object> allEdges = new ArrayList<Object>(this.graph.edgeSet());
        allEdges.sort(Comparator.comparingDouble(this.graph::getEdgeWeight));
        double spanningTreeCost = 0.0;
        HashSet<E> edgeList = new HashSet<E>();
        for (E e : allEdges) {
            V source = this.graph.getEdgeSource(e);
            V target = this.graph.getEdgeTarget(e);
            if (forest.find(source).equals(forest.find(target))) continue;
            forest.union(source, target);
            edgeList.add(e);
            spanningTreeCost += this.graph.getEdgeWeight(e);
        }
        return new SpanningTreeAlgorithm.SpanningTreeImpl(edgeList, spanningTreeCost);
    }
}

