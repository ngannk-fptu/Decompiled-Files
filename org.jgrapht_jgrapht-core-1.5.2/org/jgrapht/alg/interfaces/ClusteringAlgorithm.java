/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface ClusteringAlgorithm<V> {
    public Clustering<V> getClustering();

    public static class ClusteringImpl<V>
    implements Clustering<V>,
    Serializable {
        private static final long serialVersionUID = -5718903410443848101L;
        private final List<Set<V>> clusters;

        public ClusteringImpl(List<Set<V>> clusters) {
            this.clusters = clusters;
        }

        @Override
        public int getNumberClusters() {
            return this.clusters.size();
        }

        @Override
        public List<Set<V>> getClusters() {
            return this.clusters;
        }

        public String toString() {
            return "Clustering [k=" + this.clusters.size() + ", clusters=" + this.clusters + "]";
        }

        @Override
        public Iterator<Set<V>> iterator() {
            return this.clusters.iterator();
        }
    }

    public static interface Clustering<V>
    extends Iterable<Set<V>> {
        public int getNumberClusters();

        public List<Set<V>> getClusters();
    }
}

