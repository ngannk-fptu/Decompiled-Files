/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

public interface SpanningTreeAlgorithm<E> {
    public SpanningTree<E> getSpanningTree();

    public static class SpanningTreeImpl<E>
    implements SpanningTree<E>,
    Serializable {
        private static final long serialVersionUID = 402707108331703333L;
        private final double weight;
        private final Set<E> edges;

        public SpanningTreeImpl(Set<E> edges, double weight) {
            this.edges = edges;
            this.weight = weight;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public Set<E> getEdges() {
            return this.edges;
        }

        public String toString() {
            return "Spanning-Tree [weight=" + this.weight + ", edges=" + this.edges + "]";
        }
    }

    public static interface SpanningTree<E>
    extends Iterable<E> {
        public double getWeight();

        public Set<E> getEdges();

        @Override
        default public Iterator<E> iterator() {
            return this.getEdges().iterator();
        }
    }
}

