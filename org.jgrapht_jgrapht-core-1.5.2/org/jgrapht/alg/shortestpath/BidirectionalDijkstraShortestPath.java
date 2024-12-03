/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.BaseBidirectionalShortestPathAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public final class BidirectionalDijkstraShortestPath<V, E>
extends BaseBidirectionalShortestPathAlgorithm<V, E> {
    private double radius;
    private final Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier;

    public BidirectionalDijkstraShortestPath(Graph<V, E> graph) {
        this(graph, Double.POSITIVE_INFINITY, PairingHeap::new);
    }

    public BidirectionalDijkstraShortestPath(Graph<V, E> graph, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
        this(graph, Double.POSITIVE_INFINITY, heapSupplier);
    }

    public BidirectionalDijkstraShortestPath(Graph<V, E> graph, double radius) {
        this(graph, radius, PairingHeap::new);
    }

    public BidirectionalDijkstraShortestPath(Graph<V, E> graph, double radius, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
        super(graph);
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }
        this.heapSupplier = Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null");
        this.radius = radius;
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink) {
        return new BidirectionalDijkstraShortestPath<V, E>(graph).getPath(source, sink);
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        if (source.equals(sink)) {
            return this.createEmptyPath(source, sink);
        }
        DijkstraSearchFrontier forwardFrontier = new DijkstraSearchFrontier(this.graph, this.heapSupplier);
        DijkstraSearchFrontier<Object, E> backwardFrontier = this.graph.getType().isDirected() ? new DijkstraSearchFrontier(new EdgeReversedGraph(this.graph), this.heapSupplier) : new DijkstraSearchFrontier<Object, E>(this.graph, this.heapSupplier);
        assert (!source.equals(sink));
        forwardFrontier.updateDistance(source, null, 0.0);
        backwardFrontier.updateDistance(sink, null, 0.0);
        double bestPath = Double.POSITIVE_INFINITY;
        Object bestPathCommonVertex = null;
        DijkstraSearchFrontier frontier = forwardFrontier;
        DijkstraSearchFrontier<Object, E> otherFrontier = backwardFrontier;
        while (!(frontier.heap.isEmpty() || otherFrontier.heap.isEmpty() || (Double)frontier.heap.findMin().getKey() + (Double)otherFrontier.heap.findMin().getKey() >= bestPath)) {
            AddressableHeap.Handle node = frontier.heap.deleteMin();
            Object v = ((Pair)node.getValue()).getFirst();
            double vDistance = (Double)node.getKey();
            for (Object e : frontier.graph.outgoingEdgesOf(v)) {
                Object u = Graphs.getOppositeVertex(frontier.graph, e, v);
                double eWeight = frontier.graph.getEdgeWeight(e);
                frontier.updateDistance(u, e, vDistance + eWeight);
                double pathDistance = vDistance + eWeight + otherFrontier.getDistance(u);
                if (!(pathDistance < bestPath)) continue;
                bestPath = pathDistance;
                bestPathCommonVertex = u;
            }
            DijkstraSearchFrontier tmpFrontier = frontier;
            frontier = otherFrontier;
            otherFrontier = tmpFrontier;
        }
        if (Double.isFinite(bestPath) && bestPath <= this.radius) {
            return this.createPath(forwardFrontier, backwardFrontier, bestPath, source, bestPathCommonVertex, sink);
        }
        return this.createEmptyPath(source, sink);
    }

    static class DijkstraSearchFrontier<V, E>
    extends BaseBidirectionalShortestPathAlgorithm.BaseSearchFrontier<V, E> {
        final AddressableHeap<Double, Pair<V, E>> heap;
        final Map<V, AddressableHeap.Handle<Double, Pair<V, E>>> seen;

        DijkstraSearchFrontier(Graph<V, E> graph, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
            super(graph);
            this.heap = heapSupplier.get();
            this.seen = new HashMap<V, AddressableHeap.Handle<Double, Pair<V, E>>>();
        }

        void updateDistance(V v, E e, double distance) {
            AddressableHeap.Handle node = this.seen.get(v);
            if (node == null) {
                node = this.heap.insert((Object)distance, new Pair<V, E>(v, e));
                this.seen.put((AddressableHeap.Handle)v, (AddressableHeap.Handle<Double, Pair<AddressableHeap.Handle, E>>)node);
            } else if (distance < (Double)node.getKey()) {
                node.decreaseKey((Object)distance);
                node.setValue(Pair.of(v, e));
            }
        }

        @Override
        public double getDistance(V v) {
            AddressableHeap.Handle<Double, Pair<V, E>> node = this.seen.get(v);
            if (node == null) {
                return Double.POSITIVE_INFINITY;
            }
            return (Double)node.getKey();
        }

        @Override
        public E getTreeEdge(V v) {
            AddressableHeap.Handle<Double, Pair<V, E>> node = this.seen.get(v);
            if (node == null) {
                return null;
            }
            return (E)((Pair)node.getValue()).getSecond();
        }
    }
}

