/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.Cycles;

public interface CycleBasisAlgorithm<V, E> {
    public CycleBasis<V, E> getCycleBasis();

    public static class CycleBasisImpl<V, E>
    implements CycleBasis<V, E>,
    Serializable {
        private static final long serialVersionUID = -1420882459022219505L;
        private final Graph<V, E> graph;
        private final Set<List<E>> cycles;
        private Set<GraphPath<V, E>> graphPaths;
        private final int length;
        private final double weight;

        public CycleBasisImpl(Graph<V, E> graph) {
            this(graph, Collections.emptySet(), 0, 0.0);
        }

        public CycleBasisImpl(Graph<V, E> graph, Set<List<E>> cycles, int length, double weight) {
            this.graph = graph;
            this.cycles = Collections.unmodifiableSet(cycles);
            this.length = length;
            this.weight = weight;
        }

        @Override
        public Set<List<E>> getCycles() {
            return this.cycles;
        }

        @Override
        public int getLength() {
            return this.length;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public Set<GraphPath<V, E>> getCyclesAsGraphPaths() {
            if (this.graphPaths == null) {
                this.graphPaths = new LinkedHashSet<GraphPath<V, E>>();
                for (List<E> cycle : this.cycles) {
                    this.graphPaths.add(Cycles.simpleCycleToGraphPath(this.graph, cycle));
                }
            }
            return Collections.unmodifiableSet(this.graphPaths);
        }
    }

    public static interface CycleBasis<V, E> {
        public Set<List<E>> getCycles();

        public int getLength();

        public double getWeight();

        public Set<GraphPath<V, E>> getCyclesAsGraphPaths();
    }
}

