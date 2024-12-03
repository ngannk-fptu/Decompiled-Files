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
import org.jgrapht.alg.linkprediction.LinkPredictionIndexNotWellDefinedException;
import org.jgrapht.alg.util.Pair;

public class JaccardCoefficientLinkPrediction<V, E>
implements LinkPredictionAlgorithm<V, E> {
    private Graph<V, E> graph;

    public JaccardCoefficientLinkPrediction(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public double predict(V u, V v) {
        if (u.equals(v)) {
            return 1.0;
        }
        List<V> gu = Graphs.successorListOf(this.graph, u);
        List<V> gv = Graphs.successorListOf(this.graph, v);
        HashSet<V> union = new HashSet<V>(gu);
        union.addAll(gv);
        if (union.isEmpty()) {
            throw new LinkPredictionIndexNotWellDefinedException("Query nodes have no neighbor in common", Pair.of(u, v));
        }
        HashSet<V> intersection = new HashSet<V>(gu);
        intersection.retainAll(gv);
        return (double)intersection.size() / (double)union.size();
    }
}

