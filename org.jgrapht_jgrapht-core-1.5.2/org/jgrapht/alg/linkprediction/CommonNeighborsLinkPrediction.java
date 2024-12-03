/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.linkprediction;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.LinkPredictionAlgorithm;

public class CommonNeighborsLinkPrediction<V, E>
implements LinkPredictionAlgorithm<V, E> {
    private Graph<V, E> graph;

    public CommonNeighborsLinkPrediction(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public double predict(V u, V v) {
        List<V> gu = Graphs.successorListOf(this.graph, u);
        List<V> gv = Graphs.successorListOf(this.graph, v);
        HashSet<V> intersection = new HashSet<V>(gu);
        intersection.retainAll(gv);
        return intersection.size();
    }
}

