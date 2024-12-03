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

public class S\u00f8rensenIndexLinkPrediction<V, E>
implements LinkPredictionAlgorithm<V, E> {
    private Graph<V, E> graph;

    public S\u00f8rensenIndexLinkPrediction(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public double predict(V u, V v) {
        int dv;
        int du = this.graph.outDegreeOf(u);
        if (du + (dv = this.graph.outDegreeOf(v)) == 0) {
            throw new LinkPredictionIndexNotWellDefinedException("Both vertices have zero neighbors", Pair.of(u, v));
        }
        List<V> gu = Graphs.successorListOf(this.graph, u);
        List<V> gv = Graphs.successorListOf(this.graph, v);
        HashSet<V> intersection = new HashSet<V>(gu);
        intersection.retainAll(gv);
        return 2.0 * (double)intersection.size() / (double)(du + dv);
    }
}

