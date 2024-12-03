/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.Graph;

public interface AStarAdmissibleHeuristic<V> {
    public double getCostEstimate(V var1, V var2);

    default public <E> boolean isConsistent(Graph<V, E> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null!");
        }
        for (V targetVertex : graph.vertexSet()) {
            for (E e : graph.edgeSet()) {
                double hY;
                double weight = graph.getEdgeWeight(e);
                V edgeSource = graph.getEdgeSource(e);
                V edgeTarget = graph.getEdgeTarget(e);
                double hX = this.getCostEstimate(edgeSource, targetVertex);
                if (!(hX > weight + (hY = this.getCostEstimate(edgeTarget, targetVertex)))) continue;
                return false;
            }
        }
        return true;
    }
}

