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
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.BaseBidirectionalShortestPathAlgorithm;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class BidirectionalAStarShortestPath<V, E>
extends BaseBidirectionalShortestPathAlgorithm<V, E> {
    private AStarAdmissibleHeuristic<V> forwardHeuristic;
    private AStarAdmissibleHeuristic<V> backwardHeuristic;
    private final Supplier<AddressableHeap<Double, V>> heapSupplier;

    public BidirectionalAStarShortestPath(Graph<V, E> graph, AStarAdmissibleHeuristic<V> heuristic) {
        this(graph, heuristic, PairingHeap::new);
    }

    public BidirectionalAStarShortestPath(Graph<V, E> graph, AStarAdmissibleHeuristic<V> heuristic, Supplier<AddressableHeap<Double, V>> heapSupplier) {
        super(graph);
        this.forwardHeuristic = Objects.requireNonNull(heuristic, "Heuristic function cannot be null!");
        this.backwardHeuristic = graph.getType().isDirected() ? new ReversedGraphHeuristic(Objects.requireNonNull(heuristic, "Heuristic function cannot be null!")) : Objects.requireNonNull(heuristic, "Heuristic function cannot be null!");
        this.heapSupplier = Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null!");
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        TerminationCriterion condition;
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        if (source.equals(sink)) {
            return this.createEmptyPath(source, sink);
        }
        AStarSearchFrontier forwardFrontier = new AStarSearchFrontier(this.graph, sink, this.forwardHeuristic);
        AStarSearchFrontier backwardFrontier = this.graph.getType().isDirected() ? new AStarSearchFrontier(new EdgeReversedGraph(this.graph), source, this.backwardHeuristic) : new AStarSearchFrontier(this.graph, source, this.backwardHeuristic);
        forwardFrontier.updateDistance(source, null, 0.0, 0.0);
        backwardFrontier.updateDistance(sink, null, 0.0, 0.0);
        double bestPath = Double.POSITIVE_INFINITY;
        Object bestPathCommonVertex = null;
        AStarSearchFrontier frontier = forwardFrontier;
        AStarSearchFrontier otherFrontier = backwardFrontier;
        if (this.forwardHeuristic.isConsistent(this.graph)) {
            double sourceTargetEstimate = forwardFrontier.heuristic.getCostEstimate(source, sink);
            condition = new ConsistentTerminationCriterion(forwardFrontier, backwardFrontier, sourceTargetEstimate);
        } else {
            condition = new InconsistentTerminationCriterion(forwardFrontier, backwardFrontier);
        }
        while (!((TerminationCriterion)condition).stop(bestPath)) {
            AddressableHeap.Handle node = frontier.openList.deleteMin();
            Object v = node.getValue();
            for (Object edge : frontier.graph.outgoingEdgesOf(v)) {
                Object successor = Graphs.getOppositeVertex(frontier.graph, edge, v);
                if (successor.equals(v)) continue;
                double edgeWeight = frontier.graph.getEdgeWeight(edge);
                double gScore = frontier.getDistance(v);
                double tentativeGScore = gScore + edgeWeight;
                double fScore = tentativeGScore + frontier.heuristic.getCostEstimate(successor, frontier.endVertex);
                frontier.updateDistance(successor, edge, tentativeGScore, fScore);
                double pathDistance = gScore + edgeWeight + otherFrontier.getDistance(successor);
                if (!(pathDistance < bestPath)) continue;
                bestPath = pathDistance;
                bestPathCommonVertex = successor;
            }
            frontier.closedList.add(v);
            if (frontier.openList.size() <= otherFrontier.openList.size()) continue;
            AStarSearchFrontier tmpFrontier = frontier;
            frontier = otherFrontier;
            otherFrontier = tmpFrontier;
        }
        if (Double.isFinite(bestPath)) {
            return this.createPath(forwardFrontier, backwardFrontier, bestPath, source, bestPathCommonVertex, sink);
        }
        return this.createEmptyPath(source, sink);
    }

    class ReversedGraphHeuristic
    implements AStarAdmissibleHeuristic<V> {
        private final AStarAdmissibleHeuristic<V> heuristic;

        ReversedGraphHeuristic(AStarAdmissibleHeuristic<V> heuristic) {
            this.heuristic = heuristic;
        }

        @Override
        public double getCostEstimate(V sourceVertex, V targetVertex) {
            return this.heuristic.getCostEstimate(targetVertex, sourceVertex);
        }
    }

    class AStarSearchFrontier
    extends BaseBidirectionalShortestPathAlgorithm.BaseSearchFrontier<V, E> {
        final V endVertex;
        final AStarAdmissibleHeuristic<V> heuristic;
        final AddressableHeap<Double, V> openList;
        final Map<V, AddressableHeap.Handle<Double, V>> vertexToHeapNodeMap;
        final Set<V> closedList;
        final Map<V, Double> gScoreMap;
        final Map<V, E> cameFrom;

        AStarSearchFrontier(Graph<V, E> graph, V endVertex, AStarAdmissibleHeuristic<V> heuristic) {
            super(graph);
            this.endVertex = endVertex;
            this.heuristic = heuristic;
            this.openList = BidirectionalAStarShortestPath.this.heapSupplier.get();
            this.vertexToHeapNodeMap = new HashMap();
            this.closedList = new HashSet();
            this.gScoreMap = new HashMap();
            this.cameFrom = new HashMap();
        }

        void updateDistance(V v, E e, double tentativeGScore, double fScore) {
            AddressableHeap.Handle node = this.vertexToHeapNodeMap.get(v);
            if (this.vertexToHeapNodeMap.containsKey(v)) {
                if (tentativeGScore >= this.gScoreMap.get(v)) {
                    return;
                }
                this.cameFrom.put(v, e);
                this.gScoreMap.put((Double)v, tentativeGScore);
                if (this.closedList.contains(v)) {
                    this.closedList.remove(v);
                    this.openList.insert((Object)fScore, v);
                } else {
                    node.decreaseKey((Object)fScore);
                }
            } else {
                this.cameFrom.put(v, e);
                this.gScoreMap.put((Double)v, tentativeGScore);
                node = this.openList.insert((Object)fScore, v);
                this.vertexToHeapNodeMap.put((AddressableHeap.Handle)v, (AddressableHeap.Handle<Double, AddressableHeap.Handle>)node);
            }
        }

        @Override
        double getDistance(V v) {
            Double distance = this.gScoreMap.get(v);
            if (distance == null) {
                return Double.POSITIVE_INFINITY;
            }
            return distance;
        }

        @Override
        E getTreeEdge(V v) {
            return this.cameFrom.get(v);
        }
    }

    class ConsistentTerminationCriterion
    extends TerminationCriterion {
        final double sourceTargetEstimate;

        ConsistentTerminationCriterion(AStarSearchFrontier forward, AStarSearchFrontier backward, double sourceTargetEstimate) {
            super(forward, backward);
            this.sourceTargetEstimate = sourceTargetEstimate;
        }

        @Override
        boolean stop(double bestPath) {
            return this.forward.openList.isEmpty() || this.backward.openList.isEmpty() || (Double)this.forward.openList.findMin().getKey() + (Double)this.backward.openList.findMin().getKey() >= bestPath + this.sourceTargetEstimate;
        }
    }

    class InconsistentTerminationCriterion
    extends TerminationCriterion {
        InconsistentTerminationCriterion(AStarSearchFrontier forward, AStarSearchFrontier backward) {
            super(forward, backward);
        }

        @Override
        boolean stop(double bestPath) {
            return this.forward.openList.isEmpty() || this.backward.openList.isEmpty() || Math.max((Double)this.forward.openList.findMin().getKey(), (Double)this.backward.openList.findMin().getKey()) >= bestPath;
        }
    }

    abstract class TerminationCriterion {
        final AStarSearchFrontier forward;
        final AStarSearchFrontier backward;

        TerminationCriterion(AStarSearchFrontier forward, AStarSearchFrontier backward) {
            this.forward = forward;
            this.backward = backward;
        }

        abstract boolean stop(double var1);
    }
}

