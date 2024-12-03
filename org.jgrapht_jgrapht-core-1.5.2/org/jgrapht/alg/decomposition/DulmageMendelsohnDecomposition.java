/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.decomposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.traverse.DepthFirstIterator;

public class DulmageMendelsohnDecomposition<V, E> {
    private final Graph<V, E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;

    public DulmageMendelsohnDecomposition(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
        this.graph = Objects.requireNonNull(graph);
        this.partition1 = partition1;
        this.partition2 = partition2;
        assert (GraphTests.isBipartite(graph));
    }

    public Decomposition<V, E> getDecomposition(boolean fine) {
        HopcroftKarpMaximumCardinalityBipartiteMatching<V, E> hopkarp = new HopcroftKarpMaximumCardinalityBipartiteMatching<V, E>(this.graph, this.partition1, this.partition2);
        MatchingAlgorithm.Matching<V, E> matching = hopkarp.getMatching();
        return this.decompose(matching, fine);
    }

    public Decomposition<V, E> decompose(MatchingAlgorithm.Matching<V, E> matching, boolean fine) {
        HashSet unmatched1 = new HashSet();
        HashSet unmatched2 = new HashSet();
        this.getUnmatched(matching, unmatched1, unmatched2);
        Graph dg = this.asDirectedGraph(matching);
        HashSet subset1 = new HashSet();
        unmatched1.stream().map(v -> {
            subset1.add(v);
            return v;
        }).map(v -> new DepthFirstIterator(dg, v)).forEachOrdered(it -> {
            while (it.hasNext()) {
                subset1.add(it.next());
            }
        });
        EdgeReversedGraph gd = new EdgeReversedGraph(dg);
        HashSet subset2 = new HashSet();
        unmatched2.stream().map(v -> {
            subset2.add(v);
            return v;
        }).map(v -> new DepthFirstIterator(gd, v)).forEachOrdered(it -> {
            while (it.hasNext()) {
                subset2.add(it.next());
            }
        });
        HashSet<V> subset3 = new HashSet<V>();
        subset3.addAll(this.partition1);
        subset3.addAll(this.partition2);
        subset3.removeAll(subset1);
        subset3.removeAll(subset2);
        if (fine) {
            ArrayList out = new ArrayList();
            Graph<E, DefaultEdge> graphH = this.asDirectedEdgeGraph(matching, subset3);
            KosarajuStrongConnectivityInspector<E, DefaultEdge> sci = new KosarajuStrongConnectivityInspector<E, DefaultEdge>(graphH);
            for (Set edgeSet : sci.stronglyConnectedSets()) {
                HashSet vertexSet = new HashSet();
                edgeSet.stream().map(edge -> {
                    vertexSet.add(this.graph.getEdgeSource(edge));
                    return edge;
                }).forEachOrdered(edge -> vertexSet.add(this.graph.getEdgeTarget(edge)));
                out.add(vertexSet);
            }
            return new Decomposition(subset1, subset2, out);
        }
        return new Decomposition(subset1, subset2, Collections.singletonList(subset3));
    }

    private void getUnmatched(MatchingAlgorithm.Matching<V, E> matching, Set<V> unmatched1, Set<V> unmatched2) {
        unmatched1.addAll(this.partition1);
        unmatched2.addAll(this.partition2);
        matching.forEach(e -> {
            V source = this.graph.getEdgeSource(e);
            V target = this.graph.getEdgeTarget(e);
            if (this.partition1.contains(source)) {
                unmatched1.remove(source);
                unmatched2.remove(target);
            } else {
                unmatched2.remove(source);
                unmatched1.remove(target);
            }
        });
    }

    private Graph<V, DefaultEdge> asDirectedGraph(MatchingAlgorithm.Matching<V, E> matching) {
        GraphBuilder builder = DefaultDirectedGraph.createBuilder(DefaultEdge.class);
        this.graph.vertexSet().forEach(v -> builder.addVertex(v));
        this.graph.edgeSet().forEach(e -> {
            V v1 = this.graph.getEdgeSource(e);
            V v2 = this.graph.getEdgeTarget(e);
            if (this.partition1.contains(v1)) {
                builder.addEdge(v1, v2);
                if (matching.getEdges().contains(e)) {
                    builder.addEdge(v2, v1);
                }
            } else {
                builder.addEdge(v2, v1);
                if (matching.getEdges().contains(e)) {
                    builder.addEdge(v1, v2);
                }
            }
        });
        return builder.build();
    }

    private Graph<E, DefaultEdge> asDirectedEdgeGraph(MatchingAlgorithm.Matching<V, E> matching, Set<V> subset) {
        GraphBuilder<E, DefaultEdge, DefaultDirectedGraph<E, DefaultEdge>> graphHBuilder = DefaultDirectedGraph.createBuilder(DefaultEdge.class);
        for (E e : this.graph.edgeSet()) {
            V v1 = this.graph.getEdgeSource(e);
            V v2 = this.graph.getEdgeTarget(e);
            if (!subset.contains(v1) || !subset.contains(v2)) continue;
            if (matching.getEdges().contains(e)) {
                graphHBuilder.addVertex(e);
                continue;
            }
            Object e1 = null;
            Object e2 = null;
            for (E other : this.graph.edgesOf(v1)) {
                if (!matching.getEdges().contains(other)) continue;
                e1 = other;
                graphHBuilder.addVertex(e1);
                break;
            }
            for (E other : this.graph.edgesOf(v2)) {
                if (!matching.getEdges().contains(other)) continue;
                e2 = other;
                graphHBuilder.addVertex(e2);
                break;
            }
            graphHBuilder.addEdge(e1, e2);
        }
        return graphHBuilder.build();
    }

    public static class Decomposition<V, E> {
        private final Set<V> subset1;
        private final Set<V> subset2;
        private final List<Set<V>> subset3;

        Decomposition(Set<V> subset1, Set<V> subset2, List<Set<V>> subset3) {
            this.subset1 = subset1;
            this.subset2 = subset2;
            this.subset3 = subset3;
        }

        public Set<V> getPartition1DominatedSet() {
            return this.subset1;
        }

        public Set<V> getPartition2DominatedSet() {
            return this.subset2;
        }

        public List<Set<V>> getPerfectMatchedSets() {
            return this.subset3;
        }
    }
}

