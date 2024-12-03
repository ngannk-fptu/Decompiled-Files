/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Set;
import org.jgrapht.util.WeightedUnmodifiableSet;

public interface CliqueAlgorithm<V> {
    public Clique<V> getClique();

    public static class CliqueImpl<V>
    extends WeightedUnmodifiableSet<V>
    implements Clique<V> {
        private static final long serialVersionUID = -4336873008459736342L;

        public CliqueImpl(Set<V> clique) {
            super(clique);
        }

        public CliqueImpl(Set<V> clique, double weight) {
            super(clique, weight);
        }
    }

    public static interface Clique<V>
    extends Set<V> {
        public double getWeight();
    }
}

