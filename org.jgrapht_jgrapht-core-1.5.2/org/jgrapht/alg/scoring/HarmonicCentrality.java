/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.util.HashMap;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.scoring.ClosenessCentrality;

public final class HarmonicCentrality<V, E>
extends ClosenessCentrality<V, E> {
    public HarmonicCentrality(Graph<V, E> graph) {
        this(graph, false, true);
    }

    public HarmonicCentrality(Graph<V, E> graph, boolean incoming, boolean normalize) {
        super(graph, incoming, normalize);
    }

    @Override
    protected void compute() {
        this.scores = new HashMap();
        ShortestPathAlgorithm alg = this.getShortestPathAlgorithm();
        int n = this.graph.vertexSet().size();
        for (Object v : this.graph.vertexSet()) {
            double sum = 0.0;
            ShortestPathAlgorithm.SingleSourcePaths paths = alg.getPaths(v);
            for (Object u : this.graph.vertexSet()) {
                if (u.equals(v)) continue;
                sum += 1.0 / paths.getWeight(u);
            }
            if (this.normalize && n > 1) {
                this.scores.put(v, sum / (double)(n - 1));
                continue;
            }
            this.scores.put(v, sum);
        }
    }
}

