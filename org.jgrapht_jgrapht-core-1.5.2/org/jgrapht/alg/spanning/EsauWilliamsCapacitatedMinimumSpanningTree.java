/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.spanning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.AbstractCapacitatedMinimumSpanningTree;
import org.jgrapht.alg.util.Pair;

public class EsauWilliamsCapacitatedMinimumSpanningTree<V, E>
extends AbstractCapacitatedMinimumSpanningTree<V, E> {
    private final int numberOfOperationsParameter;
    private boolean isAlgorithmExecuted;

    public EsauWilliamsCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights, int numberOfOperationsParameter) {
        super(graph, root, capacity, weights);
        this.numberOfOperationsParameter = numberOfOperationsParameter;
        this.isAlgorithmExecuted = false;
    }

    @Override
    public CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree() {
        if (this.isAlgorithmExecuted) {
            return this.bestSolution.calculateResultingSpanningTree();
        }
        this.bestSolution = this.getSolution();
        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree cmst = this.bestSolution.calculateResultingSpanningTree();
        this.isAlgorithmExecuted = true;
        if (!cmst.isCapacitatedSpanningTree(this.graph, this.root, this.capacity, this.demands)) {
            throw new IllegalArgumentException("This graph does not have a capacitated minimum spanning tree with the given capacity and demands.");
        }
        return cmst;
    }

    protected AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation getSolution() {
        HashMap labels = new HashMap();
        HashMap partition = new HashMap();
        int counter = 0;
        for (Object v : this.graph.vertexSet()) {
            if (v == this.root) continue;
            labels.put(v, counter);
            HashSet currentPart = new HashSet();
            currentPart.add(v);
            partition.put(counter, Pair.of(currentPart, (Double)this.demands.get(v)));
            ++counter;
        }
        this.bestSolution = new AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation(this, labels, partition);
        HashMap savings = new HashMap();
        HashMap closestVertex = new HashMap();
        HashMap restrictionMap = new HashMap();
        HashMap<Integer, Object> shortestGate = new HashMap<Integer, Object>();
        HashSet vertices = new HashSet(this.graph.vertexSet());
        vertices.remove(this.root);
        while (true) {
            Iterator it = vertices.iterator();
            while (it.hasNext()) {
                Object v = it.next();
                Object closestVertexToV = this.calculateClosestVertex(v, restrictionMap, shortestGate);
                if (closestVertexToV == null) {
                    it.remove();
                    savings.remove(v);
                    continue;
                }
                closestVertex.put(v, closestVertexToV);
                savings.put(v, this.getDistance(shortestGate.getOrDefault(this.bestSolution.getLabel(v), v), this.root) - this.getDistance(v, closestVertexToV));
            }
            LinkedList bestVertices = this.getListOfBestOptions(savings);
            if (bestVertices.isEmpty()) break;
            Object vertexToMove = bestVertices.get((int)(Math.random() * (double)bestVertices.size()));
            Integer labelOfVertexToMove = this.bestSolution.getLabel(vertexToMove);
            Object closestMoveVertex = closestVertex.get(vertexToMove);
            Integer labelOfClosestMoveVertex = this.bestSolution.getLabel(closestMoveVertex);
            Object shortestGate1 = shortestGate.getOrDefault(labelOfVertexToMove, vertexToMove);
            Object shortestGate2 = shortestGate.getOrDefault(labelOfClosestMoveVertex, closestMoveVertex);
            if (this.bestSolution.getPartitionWeight(labelOfVertexToMove) < this.bestSolution.getPartitionWeight(labelOfClosestMoveVertex)) {
                this.bestSolution.moveVertices(this.bestSolution.getPartitionSet(labelOfVertexToMove), labelOfVertexToMove, labelOfClosestMoveVertex);
                if (this.getDistance(shortestGate1, this.root) < this.getDistance(shortestGate2, this.root)) {
                    shortestGate.put(labelOfClosestMoveVertex, shortestGate1);
                    continue;
                }
                shortestGate.put(labelOfClosestMoveVertex, shortestGate2);
                continue;
            }
            this.bestSolution.moveVertices(this.bestSolution.getPartitionSet(labelOfClosestMoveVertex), labelOfClosestMoveVertex, labelOfVertexToMove);
            if (this.getDistance(shortestGate1, this.root) < this.getDistance(shortestGate2, this.root)) {
                shortestGate.put(labelOfVertexToMove, shortestGate1);
                continue;
            }
            shortestGate.put(labelOfVertexToMove, shortestGate2);
        }
        AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation result = new AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation(this, labels, partition);
        result.cleanUp();
        HashSet<Integer> labelSet = new HashSet<Integer>(result.getLabels());
        for (Integer label : labelSet) {
            result.partitionSubtreesOfSubset(result.getPartitionSet(label), label);
        }
        return result;
    }

    private LinkedList<V> getListOfBestOptions(Map<V, Double> savings) {
        LinkedList<V> bestVertices = new LinkedList<V>();
        for (Map.Entry<V, Double> entry : savings.entrySet()) {
            int position = 0;
            for (Object v : bestVertices) {
                if (savings.get(v) < entry.getValue()) break;
                ++position;
            }
            if (bestVertices.size() == this.numberOfOperationsParameter) {
                if (position >= bestVertices.size()) continue;
                bestVertices.removeLast();
                bestVertices.add(position, entry.getKey());
                continue;
            }
            bestVertices.addLast(entry.getKey());
        }
        return bestVertices;
    }

    private V calculateClosestVertex(V vertex, Map<V, Set<Integer>> restrictionMap, Map<Integer, V> shortestGate) {
        V closestVertexToV1 = null;
        V shortestGateOfV = shortestGate.get(this.bestSolution.getLabel(vertex));
        double distanceToRoot = shortestGateOfV != null ? this.getDistance(shortestGateOfV, this.root) : this.getDistance(vertex, this.root);
        block0: for (Integer label : this.bestSolution.getLabels()) {
            Set part;
            Set<Integer> restrictionSet = restrictionMap.get(vertex);
            if (restrictionSet != null && restrictionSet.contains(label) || (part = this.bestSolution.getPartitionSet(label)).contains(vertex)) continue;
            for (Object v2 : part) {
                if (!this.graph.containsEdge(vertex, v2)) continue;
                double newWeight = this.bestSolution.getPartitionWeight(this.bestSolution.getLabel(v2)) + this.bestSolution.getPartitionWeight(this.bestSolution.getLabel(vertex));
                if (newWeight <= this.capacity) {
                    double currentEdgeWeight = this.getDistance(vertex, v2);
                    if (!(currentEdgeWeight < distanceToRoot)) continue;
                    closestVertexToV1 = v2;
                    distanceToRoot = currentEdgeWeight;
                    continue;
                }
                Set restriction = restrictionMap.computeIfAbsent((Set)vertex, (Function<Set, Set<Integer>>)((Function<Object, Set>)k -> new HashSet()));
                restriction.add(this.bestSolution.getLabel(v2));
                continue block0;
            }
        }
        return closestVertexToV1;
    }

    private double getDistance(V v1, V v2) {
        Object e = this.graph.getEdge(v1, v2);
        if (e == null) {
            return Double.MAX_VALUE;
        }
        return this.graph.getEdgeWeight(e);
    }
}

