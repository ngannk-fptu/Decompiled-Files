/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.graph.GraphWalk;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class AStarShortestPath<V, E>
extends BaseShortestPathAlgorithm<V, E> {
    protected final Supplier<AddressableHeap<Double, V>> heapSupplier;
    protected AddressableHeap<Double, V> openList;
    protected Map<V, AddressableHeap.Handle<Double, V>> vertexToHeapNodeMap;
    protected Set<V> closedList;
    protected Map<V, Double> gScoreMap;
    protected Map<V, E> cameFrom;
    protected AStarAdmissibleHeuristic<V> admissibleHeuristic;
    protected int numberOfExpandedNodes;
    protected Comparator<Double> comparator;

    public AStarShortestPath(Graph<V, E> graph, AStarAdmissibleHeuristic<V> admissibleHeuristic) {
        this(graph, admissibleHeuristic, PairingHeap::new);
    }

    public AStarShortestPath(Graph<V, E> graph, AStarAdmissibleHeuristic<V> admissibleHeuristic, Supplier<AddressableHeap<Double, V>> heapSupplier) {
        super(graph);
        this.admissibleHeuristic = Objects.requireNonNull(admissibleHeuristic, "Heuristic function cannot be null!");
        this.comparator = new ToleranceDoubleComparator();
        this.heapSupplier = Objects.requireNonNull(heapSupplier, "Heap supplier cannot be null!");
    }

    private void initialize(AStarAdmissibleHeuristic<V> admissibleHeuristic) {
        this.admissibleHeuristic = admissibleHeuristic;
        this.openList = this.heapSupplier.get();
        this.vertexToHeapNodeMap = new HashMap<V, AddressableHeap.Handle<Double, V>>();
        this.closedList = new HashSet<V>();
        this.gScoreMap = new HashMap<V, Double>();
        this.cameFrom = new HashMap<V, E>();
        this.numberOfExpandedNodes = 0;
    }

    @Override
    public GraphPath<V, E> getPath(V sourceVertex, V targetVertex) {
        if (!this.graph.containsVertex(sourceVertex) || !this.graph.containsVertex(targetVertex)) {
            throw new IllegalArgumentException("Source or target vertex not contained in the graph!");
        }
        if (sourceVertex.equals(targetVertex)) {
            return this.createEmptyPath(sourceVertex, targetVertex);
        }
        this.initialize(this.admissibleHeuristic);
        this.gScoreMap.put((Double)sourceVertex, 0.0);
        AddressableHeap.Handle heapNode = this.openList.insert((Object)0.0, sourceVertex);
        this.vertexToHeapNodeMap.put((AddressableHeap.Handle)sourceVertex, (AddressableHeap.Handle<Double, AddressableHeap.Handle>)heapNode);
        do {
            AddressableHeap.Handle currentNode;
            if ((currentNode = this.openList.deleteMin()).getValue().equals(targetVertex)) {
                return this.buildGraphPath(sourceVertex, targetVertex, (Double)currentNode.getKey());
            }
            this.expandNode(currentNode, targetVertex);
            this.closedList.add(currentNode.getValue());
        } while (!this.openList.isEmpty());
        return this.createEmptyPath(sourceVertex, targetVertex);
    }

    public int getNumberOfExpandedNodes() {
        return this.numberOfExpandedNodes;
    }

    private void expandNode(AddressableHeap.Handle<Double, V> currentNode, V endVertex) {
        ++this.numberOfExpandedNodes;
        Set outgoingEdges = this.graph.outgoingEdgesOf(currentNode.getValue());
        for (Object edge : outgoingEdges) {
            Object successor = Graphs.getOppositeVertex(this.graph, edge, currentNode.getValue());
            if (successor.equals(currentNode.getValue())) continue;
            double gScore = this.gScoreMap.get(currentNode.getValue());
            double tentativeGScore = gScore + this.graph.getEdgeWeight(edge);
            double fScore = tentativeGScore + this.admissibleHeuristic.getCostEstimate(successor, endVertex);
            if (this.vertexToHeapNodeMap.containsKey(successor)) {
                if (tentativeGScore >= this.gScoreMap.get(successor)) continue;
                this.cameFrom.put(successor, edge);
                this.gScoreMap.put((Double)successor, tentativeGScore);
                if (this.closedList.contains(successor)) {
                    this.closedList.remove(successor);
                    this.openList.insert((Object)fScore, this.vertexToHeapNodeMap.get(successor).getValue());
                    continue;
                }
                this.vertexToHeapNodeMap.get(successor).decreaseKey((Object)fScore);
                continue;
            }
            this.cameFrom.put(successor, edge);
            this.gScoreMap.put((Double)successor, tentativeGScore);
            AddressableHeap.Handle heapNode = this.openList.insert((Object)fScore, successor);
            this.vertexToHeapNodeMap.put((AddressableHeap.Handle)successor, (AddressableHeap.Handle<Double, AddressableHeap.Handle>)heapNode);
        }
    }

    private GraphPath<V, E> buildGraphPath(V startVertex, V targetVertex, double pathLength) {
        ArrayList<E> edgeList = new ArrayList<E>();
        ArrayList<V> vertexList = new ArrayList<V>();
        vertexList.add(targetVertex);
        V v = targetVertex;
        while (!v.equals(startVertex)) {
            edgeList.add(this.cameFrom.get(v));
            v = Graphs.getOppositeVertex(this.graph, this.cameFrom.get(v), v);
            vertexList.add(v);
        }
        Collections.reverse(edgeList);
        Collections.reverse(vertexList);
        return new GraphWalk(this.graph, startVertex, targetVertex, vertexList, edgeList, pathLength);
    }
}

