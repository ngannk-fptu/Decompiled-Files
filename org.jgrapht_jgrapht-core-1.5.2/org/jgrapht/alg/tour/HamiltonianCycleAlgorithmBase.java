/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.MaskSubgraph;
import org.jgrapht.traverse.DepthFirstIterator;

public abstract class HamiltonianCycleAlgorithmBase<V, E>
implements HamiltonianCycleAlgorithm<V, E> {
    protected GraphPath<V, E> vertexListToTour(List<V> tour, Graph<V, E> graph) {
        tour.add(tour.get(0));
        return this.closedVertexListToTour(tour, graph);
    }

    protected GraphPath<V, E> closedVertexListToTour(List<V> tour, Graph<V, E> graph) {
        assert (tour.get(0) == tour.get(tour.size() - 1));
        ArrayList<E> edges = new ArrayList<E>(tour.size() - 1);
        double tourWeight = 0.0;
        V u = tour.get(0);
        for (V v : tour.subList(1, tour.size())) {
            E e = graph.getEdge(u, v);
            edges.add(e);
            tourWeight += graph.getEdgeWeight(e);
            u = v;
        }
        return new GraphWalk<V, E>(graph, tour.get(0), tour.get(0), tour, edges, tourWeight);
    }

    protected GraphPath<V, E> edgeSetToTour(Set<E> tour, Graph<V, E> graph) {
        ArrayList vertices = new ArrayList(tour.size() + 1);
        MaskSubgraph<Object, Object> tourGraph = new MaskSubgraph<Object, Object>(graph, v -> false, e -> !tour.contains(e));
        new DepthFirstIterator<Object, Object>(tourGraph).forEachRemaining(vertices::add);
        return this.vertexListToTour(vertices, graph);
    }

    protected GraphPath<V, E> getSingletonTour(Graph<V, E> graph) {
        assert (graph.vertexSet().size() == 1);
        V start = graph.vertexSet().iterator().next();
        return new GraphWalk<V, E>(graph, start, start, Collections.singletonList(start), Collections.emptyList(), 0.0);
    }

    protected void checkGraph(Graph<V, E> graph) {
        GraphTests.requireUndirected(graph);
        this.requireNotEmpty(graph);
        if (!GraphTests.isComplete(graph)) {
            throw new IllegalArgumentException("Graph is not complete");
        }
    }

    protected void requireNotEmpty(Graph<V, E> graph) {
        if (graph.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("Graph contains no vertices");
        }
    }
}

