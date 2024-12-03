/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;

public interface BipartiteMatchingProblem<V, E> {
    public Graph<V, E> getGraph();

    public Set<V> getPartition1();

    public Set<V> getPartition2();

    public Function<E, Double> getCosts();

    public boolean isWeighted();

    default public void dumpCosts() {
        Graph<V, E> graph = this.getGraph();
        Function<E, Double> costs = this.getCosts();
        for (E edge : graph.edgeSet()) {
            graph.setEdgeWeight(edge, costs.apply(edge));
        }
    }

    public static class BipartiteMatchingProblemImpl<V, E>
    implements BipartiteMatchingProblem<V, E> {
        private final Graph<V, E> graph;
        private final Set<V> partition1;
        private final Set<V> partition2;
        private final Function<E, Double> costs;
        private final boolean weighted;

        public BipartiteMatchingProblemImpl(Graph<V, E> graph, Set<V> partition1, Set<V> partition2, Function<E, Double> costs, boolean weighted) {
            this.graph = graph;
            this.partition1 = partition1;
            this.partition2 = partition2;
            this.costs = costs;
            this.weighted = weighted;
        }

        @Override
        public Graph<V, E> getGraph() {
            return this.graph;
        }

        @Override
        public Function<E, Double> getCosts() {
            return this.costs;
        }

        @Override
        public Set<V> getPartition1() {
            return this.partition1;
        }

        @Override
        public Set<V> getPartition2() {
            return this.partition2;
        }

        @Override
        public boolean isWeighted() {
            return this.weighted;
        }
    }
}

