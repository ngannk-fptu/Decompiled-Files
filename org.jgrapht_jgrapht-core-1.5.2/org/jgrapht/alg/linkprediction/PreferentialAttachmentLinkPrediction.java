/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.linkprediction;

import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.LinkPredictionAlgorithm;

public class PreferentialAttachmentLinkPrediction<V, E>
implements LinkPredictionAlgorithm<V, E> {
    private Graph<V, E> graph;

    public PreferentialAttachmentLinkPrediction(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    @Override
    public double predict(V u, V v) {
        int du = this.graph.outDegreeOf(u);
        int dv = this.graph.outDegreeOf(v);
        return du * dv;
    }
}

