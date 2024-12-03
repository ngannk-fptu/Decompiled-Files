/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;

public interface MaximumFlowProblem<V, E> {
    public static final double CAPACITY_INF = 2.147483647E9;

    public Graph<V, E> getGraph();

    public Set<V> getSources();

    public Set<V> getSinks();

    default public V getSource() {
        return this.getSources().iterator().next();
    }

    default public V getSink() {
        return this.getSinks().iterator().next();
    }

    public Function<E, Double> getCapacities();

    public MaximumFlowProblem<V, E> toSingleSourceSingleSinkProblem();

    default public boolean isSingleSourceSingleSinkProblem() {
        return this.getSources().size() == 1 && this.getSinks().size() == 1;
    }

    default public void dumpCapacities() {
        Graph<V, E> graph = this.getGraph();
        Function<E, Double> capacities = this.getCapacities();
        for (E edge : graph.edgeSet()) {
            graph.setEdgeWeight(edge, capacities.apply(edge));
        }
    }

    public static class MaximumFlowProblemImpl<V, E>
    implements MaximumFlowProblem<V, E> {
        private final Graph<V, E> graph;
        private final Set<V> sources;
        private final Set<V> sinks;
        private final Function<E, Double> capacities;

        public MaximumFlowProblemImpl(Graph<V, E> graph, Set<V> sources, Set<V> sinks, Function<E, Double> capacities) {
            this.graph = graph;
            this.sources = sources;
            this.sinks = sinks;
            this.capacities = capacities;
        }

        @Override
        public Graph<V, E> getGraph() {
            return this.graph;
        }

        @Override
        public Set<V> getSources() {
            return this.sources;
        }

        @Override
        public Set<V> getSinks() {
            return this.sinks;
        }

        @Override
        public Function<E, Double> getCapacities() {
            return this.capacities;
        }

        @Override
        public MaximumFlowProblem<V, E> toSingleSourceSingleSinkProblem() {
            HashSet newEdges = new HashSet();
            Set<V> sourceSet = this.convert(this.sources, newEdges, true);
            Set<V> sinkSet = this.convert(this.sinks, newEdges, false);
            Function<Object, Double> updatedCapacities = e -> {
                if (newEdges.contains(e)) {
                    return 2.147483647E9;
                }
                return this.capacities.apply(e);
            };
            return new MaximumFlowProblemImpl<V, Object>(this.graph, sourceSet, sinkSet, updatedCapacities);
        }

        private Set<V> convert(Set<V> vertices, Set<E> newEdges, boolean sources) {
            if (vertices.size() == 1) {
                return vertices;
            }
            V superVertex = this.graph.addVertex();
            Set<V> newSourceSet = Collections.singleton(superVertex);
            for (V vertex : vertices) {
                E edge = sources ? this.graph.addEdge(superVertex, vertex) : this.graph.addEdge(vertex, superVertex);
                newEdges.add(edge);
            }
            return newSourceSet;
        }
    }
}

