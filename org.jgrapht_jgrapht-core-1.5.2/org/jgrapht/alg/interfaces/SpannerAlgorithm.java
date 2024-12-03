/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.Set;
import org.jgrapht.util.WeightedUnmodifiableSet;

public interface SpannerAlgorithm<E> {
    public Spanner<E> getSpanner();

    public static class SpannerImpl<E>
    extends WeightedUnmodifiableSet<E>
    implements Spanner<E>,
    Serializable {
        private static final long serialVersionUID = 5951646499902668516L;

        public SpannerImpl(Set<E> edges) {
            super(edges);
        }

        public SpannerImpl(Set<E> edges, double weight) {
            super(edges, weight);
        }

        @Override
        public String toString() {
            return "Spanner [weight=" + this.weight + ", edges=" + this + "]";
        }
    }

    public static interface Spanner<E>
    extends Set<E> {
        public double getWeight();
    }
}

