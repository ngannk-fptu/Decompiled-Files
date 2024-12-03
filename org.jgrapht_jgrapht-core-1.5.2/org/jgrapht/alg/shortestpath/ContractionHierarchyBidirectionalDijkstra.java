/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.MaskSubgraph;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class ContractionHierarchyBidirectionalDijkstra<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    private ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> contractionHierarchy;
    private Graph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> contractionGraph;
    private Map<V, ContractionHierarchyPrecomputation.ContractionVertex<V>> contractionMapping;
    private Supplier<AddressableHeap<Double, Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> heapSupplier;
    private double radius;

    public ContractionHierarchyBidirectionalDijkstra(Graph<V, E> graph, ThreadPoolExecutor executor) {
        this(new ContractionHierarchyPrecomputation<V, E>(graph, executor).computeContractionHierarchy());
    }

    public ContractionHierarchyBidirectionalDijkstra(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> hierarchy) {
        this(hierarchy, Double.POSITIVE_INFINITY, PairingHeap::new);
    }

    public ContractionHierarchyBidirectionalDijkstra(ContractionHierarchyPrecomputation.ContractionHierarchy<V, E> hierarchy, double radius, Supplier<AddressableHeap<Double, Pair<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>>> heapSupplier) {
        super(hierarchy.getGraph());
        this.contractionHierarchy = hierarchy;
        this.contractionGraph = hierarchy.getContractionGraph();
        this.contractionMapping = hierarchy.getContractionMapping();
        this.radius = radius;
        this.heapSupplier = heapSupplier;
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
        ContractionHierarchyPrecomputation.ContractionVertex<V> contractedSource = this.contractionMapping.get(source);
        ContractionHierarchyPrecomputation.ContractionVertex<V> contractedSink = this.contractionMapping.get(sink);
        ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge>> forwardFrontier = new ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge>>(new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge>(this.contractionGraph, v -> false, e -> !e.isUpward), this.heapSupplier);
        ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex<V>>>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge<Object>>> backwardFrontier = new ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex<V>>>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge<Object>>>(new MaskSubgraph<ContractionHierarchyPrecomputation.ContractionVertex, ContractionHierarchyPrecomputation.ContractionEdge>(new EdgeReversedGraph<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>>(this.contractionGraph), v -> false, e -> e.isUpward), this.heapSupplier);
        forwardFrontier.updateDistance(contractedSource, null, 0.0);
        backwardFrontier.updateDistance(contractedSink, null, 0.0);
        double bestPath = Double.POSITIVE_INFINITY;
        ContractionHierarchyPrecomputation.ContractionVertex bestPathCommonVertex = null;
        ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge>> frontier = forwardFrontier;
        ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex>>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge<E>>> otherFrontier = backwardFrontier;
        while (true) {
            if (frontier.heap.isEmpty()) {
                frontier.isFinished = true;
            }
            if (otherFrontier.heap.isEmpty()) {
                otherFrontier.isFinished = true;
            }
            if (frontier.isFinished && otherFrontier.isFinished) break;
            if ((Double)frontier.heap.findMin().getKey() >= bestPath) {
                frontier.isFinished = true;
            } else {
                AddressableHeap.Handle node = frontier.heap.deleteMin();
                ContractionHierarchyPrecomputation.ContractionVertex v2 = (ContractionHierarchyPrecomputation.ContractionVertex)((Pair)node.getValue()).getFirst();
                double vDistance = (Double)node.getKey();
                for (ContractionHierarchyPrecomputation.ContractionEdge e2 : frontier.graph.outgoingEdgesOf(v2)) {
                    ContractionHierarchyPrecomputation.ContractionVertex u = (ContractionHierarchyPrecomputation.ContractionVertex)frontier.graph.getEdgeTarget(e2);
                    double eWeight = frontier.graph.getEdgeWeight(e2);
                    frontier.updateDistance(u, e2, vDistance + eWeight);
                    double pathDistance = vDistance + eWeight + otherFrontier.getDistance(u);
                    if (!(pathDistance < bestPath)) continue;
                    bestPath = pathDistance;
                    bestPathCommonVertex = u;
                }
            }
            if (otherFrontier.isFinished) continue;
            ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<ContractionHierarchyPrecomputation.ContractionVertex>, ContractionHierarchyPrecomputation.ContractionEdge<ContractionHierarchyPrecomputation.ContractionEdge>> tmpFrontier = frontier;
            frontier = otherFrontier;
            otherFrontier = tmpFrontier;
        }
        if (Double.isFinite(bestPath) && bestPath <= this.radius) {
            return this.createPath(forwardFrontier, backwardFrontier, bestPath, contractedSource, bestPathCommonVertex, contractedSink);
        }
        return this.createEmptyPath(source, sink);
    }

    private GraphPath<V, E> createPath(ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> forwardFrontier, ContractionSearchFrontier<ContractionHierarchyPrecomputation.ContractionVertex<V>, ContractionHierarchyPrecomputation.ContractionEdge<E>> backwardFrontier, double weight, ContractionHierarchyPrecomputation.ContractionVertex<V> source, ContractionHierarchyPrecomputation.ContractionVertex<V> commonVertex, ContractionHierarchyPrecomputation.ContractionVertex<V> sink) {
        ContractionHierarchyPrecomputation.ContractionEdge e;
        LinkedList edgeList = new LinkedList();
        LinkedList vertexList = new LinkedList();
        vertexList.add(commonVertex.vertex);
        ContractionHierarchyPrecomputation.ContractionVertex<V> v = commonVertex;
        while ((e = (ContractionHierarchyPrecomputation.ContractionEdge)forwardFrontier.getTreeEdge(v)) != null) {
            this.contractionHierarchy.unpackBackward(e, vertexList, edgeList);
            v = this.contractionGraph.getEdgeSource(e);
        }
        v = commonVertex;
        while ((e = (ContractionHierarchyPrecomputation.ContractionEdge)backwardFrontier.getTreeEdge(v)) != null) {
            this.contractionHierarchy.unpackForward(e, vertexList, edgeList);
            v = this.contractionGraph.getEdgeTarget(e);
        }
        return new GraphWalk(this.graph, source.vertex, sink.vertex, vertexList, edgeList, weight);
    }

    static class ContractionSearchFrontier<V, E>
    extends BidirectionalDijkstraShortestPath.DijkstraSearchFrontier<V, E> {
        boolean isFinished;

        ContractionSearchFrontier(Graph<V, E> graph, Supplier<AddressableHeap<Double, Pair<V, E>>> heapSupplier) {
            super(graph, heapSupplier);
        }
    }
}

