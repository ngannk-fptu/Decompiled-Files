/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

public class GreedyMaximumCardinalityMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final boolean sort;

    public GreedyMaximumCardinalityMatching(Graph<V, E> graph, boolean sort) {
        this.graph = GraphTests.requireUndirected(graph);
        this.sort = sort;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        HashSet<V> matched = new HashSet<V>();
        LinkedHashSet<E> edges = new LinkedHashSet<E>();
        double cost = 0.0;
        if (this.sort) {
            ArrayList<E> allEdges = new ArrayList<E>(this.graph.edgeSet());
            allEdges.sort(new EdgeDegreeComparator());
            for (Object e : allEdges) {
                V w;
                V v = this.graph.getEdgeSource(e);
                if (v.equals(w = this.graph.getEdgeTarget(e)) || matched.contains(v) || matched.contains(w)) continue;
                edges.add(e);
                matched.add(v);
                matched.add(w);
                cost += this.graph.getEdgeWeight(e);
            }
        } else {
            block1: for (V v : this.graph.vertexSet()) {
                if (matched.contains(v)) continue;
                for (E e : this.graph.edgesOf(v)) {
                    V w = Graphs.getOppositeVertex(this.graph, e, v);
                    if (v.equals(w) || matched.contains(w)) continue;
                    edges.add(e);
                    matched.add(v);
                    matched.add(w);
                    cost += this.graph.getEdgeWeight(e);
                    continue block1;
                }
            }
        }
        return new MatchingAlgorithm.MatchingImpl<V, E>(this.graph, edges, cost);
    }

    private class EdgeDegreeComparator
    implements Comparator<E> {
        private EdgeDegreeComparator() {
        }

        @Override
        public int compare(E e1, E e2) {
            int degreeE1 = GreedyMaximumCardinalityMatching.this.graph.degreeOf(GreedyMaximumCardinalityMatching.this.graph.getEdgeSource(e1)) + GreedyMaximumCardinalityMatching.this.graph.degreeOf(GreedyMaximumCardinalityMatching.this.graph.getEdgeTarget(e1));
            int degreeE2 = GreedyMaximumCardinalityMatching.this.graph.degreeOf(GreedyMaximumCardinalityMatching.this.graph.getEdgeSource(e2)) + GreedyMaximumCardinalityMatching.this.graph.degreeOf(GreedyMaximumCardinalityMatching.this.graph.getEdgeTarget(e2));
            return Integer.compare(degreeE1, degreeE2);
        }
    }
}

