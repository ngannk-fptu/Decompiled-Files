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

public class ResourceAllocationIndexLinkPrediction<V, E>
implements LinkPredictionAlgorithm<V, E> {
    private Graph<V, E> graph;

    public ResourceAllocationIndexLinkPrediction(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public double predict(V u, V v) {
        List<V> gu = Graphs.successorListOf(this.graph, u);
        List<V> gv = Graphs.successorListOf(this.graph, v);
        HashSet<V> intersection = new HashSet<V>(gu);
        intersection.retainAll(gv);
        double result = 0.0;
        for (Object z : intersection) {
            int dz = this.graph.outDegreeOf(z);
            if (dz == 0) {
                throw new LinkPredictionIndexNotWellDefinedException("Index not well defined", Pair.of(u, v));
            }
            result += 1.0 / (double)dz;
        }
        return result;
    }
}

