/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Set;
import org.jgrapht.util.WeightedUnmodifiableSet;

public interface VertexCoverAlgorithm<V> {
    public VertexCover<V> getVertexCover();

    public static class VertexCoverImpl<V>
    extends WeightedUnmodifiableSet<V>
    implements VertexCover<V> {
        private static final long serialVersionUID = 3922451519162460179L;

        public VertexCoverImpl(Set<V> vertexCover) {
            super(vertexCover);
        }

        public VertexCoverImpl(Set<V> vertexCover, double weight) {
            super(vertexCover, weight);
        }
    }

    public static interface VertexCover<V>
    extends Set<V> {
        public double getWeight();
    }
}

