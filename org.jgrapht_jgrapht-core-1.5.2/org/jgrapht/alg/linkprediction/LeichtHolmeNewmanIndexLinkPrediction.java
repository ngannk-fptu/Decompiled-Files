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

public class LeichtHolmeNewmanIndexLinkPrediction<V, E>
implements LinkPredictionAlgorithm<V, E> {
    private Graph<V, E> graph;

    public LeichtHolmeNewmanIndexLinkPrediction(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public double predict(V u, V v) {
        int du = this.graph.outDegreeOf(u);
        int dv = this.graph.outDegreeOf(v);
        if (du == 0 || dv == 0) {
            throw new LinkPredictionIndexNotWellDefinedException("Query vertex with zero neighbors", Pair.of(u, v));
        }
        List<V> gu = Graphs.successorListOf(this.graph, u);
        List<V> gv = Graphs.successorListOf(this.graph, v);
        HashSet<V> intersection = new HashSet<V>(gu);
        intersection.retainAll(gv);
        return (double)intersection.size() / (double)du * (double)dv;
    }
}

