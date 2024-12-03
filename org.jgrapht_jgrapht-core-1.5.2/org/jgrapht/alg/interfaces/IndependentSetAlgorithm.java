/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Set;
import org.jgrapht.util.WeightedUnmodifiableSet;

public interface IndependentSetAlgorithm<V> {
    public IndependentSet<V> getIndependentSet();

    public static class IndependentSetImpl<V>
    extends WeightedUnmodifiableSet<V>
    implements IndependentSet<V> {
        private static final long serialVersionUID = 4572451196544323306L;

        public IndependentSetImpl(Set<V> independentSet) {
            super(independentSet);
        }

        public IndependentSetImpl(Set<V> independentSet, double weight) {
            super(independentSet, weight);
        }
    }

    public static interface IndependentSet<V>
    extends Set<V> {
        public double getWeight();
    }
}

