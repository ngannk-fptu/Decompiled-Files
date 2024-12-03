/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jgrapht.Graph;

public interface MatchingAlgorithm<V, E> {
    public static final double DEFAULT_EPSILON = 1.0E-9;

    public Matching<V, E> getMatching();

    public static class MatchingImpl<V, E>
    implements Matching<V, E>,
    Serializable {
        private static final long serialVersionUID = 4767675421846527768L;
        private Graph<V, E> graph;
        private Set<E> edges;
        private double weight;
        private Set<V> matchedVertices = null;

        public MatchingImpl(Graph<V, E> graph, Set<E> edges, double weight) {
            this.graph = graph;
            this.edges = edges;
            this.weight = weight;
        }

        @Override
        public Graph<V, E> getGraph() {
            return this.graph;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public Set<E> getEdges() {
            return this.edges;
        }

        @Override
        public boolean isMatched(V v) {
            if (this.matchedVertices == null) {
                this.matchedVertices = new HashSet<V>();
                for (E e : this.edges) {
                    this.matchedVertices.add(this.graph.getEdgeSource(e));
                    this.matchedVertices.add(this.graph.getEdgeTarget(e));
                }
            }
            return this.matchedVertices.contains(v);
        }

        public String toString() {
            return "Matching [edges=" + this.edges + ", weight=" + this.weight + "]";
        }
    }

    public static interface Matching<V, E>
    extends Iterable<E> {
        public Graph<V, E> getGraph();

        public double getWeight();

        public Set<E> getEdges();

        default public boolean isMatched(V v) {
            Set<E> edges = this.getEdges();
            return this.getGraph().edgesOf(v).stream().anyMatch(edges::contains);
        }

        default public boolean isPerfect() {
            return (double)this.getEdges().size() == (double)this.getGraph().vertexSet().size() / 2.0;
        }

        @Override
        default public Iterator<E> iterator() {
            return this.getEdges().iterator();
        }
    }
}

