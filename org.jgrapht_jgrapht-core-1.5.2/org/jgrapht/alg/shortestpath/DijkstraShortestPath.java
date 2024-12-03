/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraClosestFirstIterator;
import org.jgrapht.alg.util.Pair;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public final class DijkstraShortestPath<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    private final double radius;
    private final Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier;

    public DijkstraShortestPath(Graph<V, E> graph) {
        this(graph, Double.POSITIVE_INFINITY, PairingHeap::new);
    }

    public DijkstraShortestPath(Graph<V, E> graph, double radius) {
        this(graph, radius, PairingHeap::new);
    }

    public DijkstraShortestPath(Graph<V, E> graph, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
        this(graph, Double.POSITIVE_INFINITY, heapSupplier);
    }

    public DijkstraShortestPath(Graph<V, E> graph, double radius, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
        super(graph);
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }
        this.heapSupplier = heapSupplier;
        this.radius = radius;
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink) {
        return new DijkstraShortestPath<V, E>(graph).getPath(source, sink);
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        V vertex;
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        if (source.equals(sink)) {
            return this.createEmptyPath(source, sink);
        }
        DijkstraClosestFirstIterator<V, E> it = new DijkstraClosestFirstIterator<V, E>(this.graph, source, this.radius, this.heapSupplier);
        while (it.hasNext() && !(vertex = it.next()).equals(sink)) {
        }
        return it.getPaths().getPath(sink);
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        DijkstraClosestFirstIterator<V, E> it = new DijkstraClosestFirstIterator<V, E>(this.graph, source, this.radius, this.heapSupplier);
        while (it.hasNext()) {
            it.next();
        }
        return it.getPaths();
    }
}

