/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow.mincost;

import java.util.Objects;
import java.util.function.Function;
import org.jgrapht.Graph;

public interface MinimumCostFlowProblem<V, E> {
    public Graph<V, E> getGraph();

    public Function<V, Integer> getNodeSupply();

    public Function<E, Integer> getArcCapacityLowerBounds();

    public Function<E, Integer> getArcCapacityUpperBounds();

    public Function<E, Double> getArcCosts();

    public static class MinimumCostFlowProblemImpl<V, E>
    implements MinimumCostFlowProblem<V, E> {
        private final Graph<V, E> graph;
        private final Function<V, Integer> nodeSupplies;
        private final Function<E, Integer> arcCapacityLowerBounds;
        private final Function<E, Integer> arcCapacityUpperBounds;
        private final Function<E, Double> arcCosts;

        public MinimumCostFlowProblemImpl(Graph<V, E> graph, Function<V, Integer> supplyMap, Function<E, Integer> arcCapacityUpperBounds) {
            this(graph, supplyMap, arcCapacityUpperBounds, a -> 0);
        }

        public MinimumCostFlowProblemImpl(Graph<V, E> graph, Function<V, Integer> nodeSupplies, Function<E, Integer> arcCapacityUpperBounds, Function<E, Integer> arcCapacityLowerBounds) {
            this(graph, nodeSupplies, arcCapacityUpperBounds, arcCapacityLowerBounds, graph::getEdgeWeight);
        }

        public MinimumCostFlowProblemImpl(Graph<V, E> graph, Function<V, Integer> nodeSupplies, Function<E, Integer> arcCapacityUpperBounds, Function<E, Integer> arcCapacityLowerBounds, Function<E, Double> arcCosts) {
            this.graph = Objects.requireNonNull(graph);
            this.nodeSupplies = Objects.requireNonNull(nodeSupplies);
            this.arcCapacityUpperBounds = Objects.requireNonNull(arcCapacityUpperBounds);
            this.arcCapacityLowerBounds = Objects.requireNonNull(arcCapacityLowerBounds);
            this.arcCosts = Objects.requireNonNull(arcCosts);
        }

        @Override
        public Graph<V, E> getGraph() {
            return this.graph;
        }

        @Override
        public Function<V, Integer> getNodeSupply() {
            return this.nodeSupplies;
        }

        @Override
        public Function<E, Integer> getArcCapacityLowerBounds() {
            return this.arcCapacityLowerBounds;
        }

        @Override
        public Function<E, Integer> getArcCapacityUpperBounds() {
            return this.arcCapacityUpperBounds;
        }

        @Override
        public Function<E, Double> getArcCosts() {
            return this.arcCosts;
        }
    }
}

