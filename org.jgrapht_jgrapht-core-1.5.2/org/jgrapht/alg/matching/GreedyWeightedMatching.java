/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public class GreedyWeightedMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;
    private final boolean normalizeEdgeCosts;

    public GreedyWeightedMatching(Graph<V, E> graph, boolean normalizeEdgeCosts) {
        this(graph, normalizeEdgeCosts, 1.0E-9);
    }

    public GreedyWeightedMatching(Graph<V, E> graph, boolean normalizeEdgeCosts, double epsilon) {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        this.graph = graph;
        this.comparator = new ToleranceDoubleComparator(epsilon);
        this.normalizeEdgeCosts = normalizeEdgeCosts;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        ArrayList<E> allEdges = new ArrayList<E>(this.graph.edgeSet());
        if (this.normalizeEdgeCosts) {
            allEdges.sort((e1, e2) -> {
                double degreeE1 = this.graph.degreeOf(this.graph.getEdgeSource(e1)) + this.graph.degreeOf(this.graph.getEdgeTarget(e1));
                double degreeE2 = this.graph.degreeOf(this.graph.getEdgeSource(e2)) + this.graph.degreeOf(this.graph.getEdgeTarget(e2));
                return this.comparator.compare(this.graph.getEdgeWeight(e2) / degreeE2, this.graph.getEdgeWeight(e1) / degreeE1);
            });
        } else {
            allEdges.sort((e1, e2) -> this.comparator.compare(this.graph.getEdgeWeight(e2), this.graph.getEdgeWeight(e1)));
        }
        double matchingWeight = 0.0;
        HashSet matching = new HashSet();
        HashSet<V> matchedVertices = new HashSet<V>();
        for (Object e : allEdges) {
            V t;
            double edgeWeight = this.graph.getEdgeWeight(e);
            V s = this.graph.getEdgeSource(e);
            if (s.equals(t = this.graph.getEdgeTarget(e)) || this.comparator.compare(edgeWeight, 0.0) <= 0 || matchedVertices.contains(s) || matchedVertices.contains(t)) continue;
            matching.add(e);
            matchedVertices.add(s);
            matchedVertices.add(t);
            matchingWeight += edgeWeight;
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, matching, matchingWeight);
    }
}

