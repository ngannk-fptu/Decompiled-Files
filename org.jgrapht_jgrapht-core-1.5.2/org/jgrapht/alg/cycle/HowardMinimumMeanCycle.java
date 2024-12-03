/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.MinimumCycleMeanAlgorithm;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.CollectionUtil;

public class HowardMinimumMeanCycle<V, E>
implements MinimumCycleMeanAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private final StrongConnectivityAlgorithm<V, E> strongConnectivityAlgorithm;
    private final int maximumIterations;
    private final Comparator<Double> comparator;
    private boolean isCurrentCycleFound;
    private double currentCycleWeight;
    private int currentCycleLength;
    private V currentCycleVertex;
    private Map<V, E> policyGraph;
    private Map<V, Boolean> reachedVertices;
    private Map<V, Integer> vertexLevel;
    private Map<V, Double> vertexDistance;

    public HowardMinimumMeanCycle(Graph<V, E> graph) {
        this(graph, Integer.MAX_VALUE);
    }

    public HowardMinimumMeanCycle(Graph<V, E> graph, int maximumIterations) {
        this(graph, maximumIterations, new GabowStrongConnectivityInspector<V, E>(graph), 1.0E-9);
    }

    public HowardMinimumMeanCycle(Graph<V, E> graph, int maximumIterations, StrongConnectivityAlgorithm<V, E> strongConnectivityAlgorithm, double toleranceEpsilon) {
        this.graph = Objects.requireNonNull(graph, "graph should not be null!");
        this.strongConnectivityAlgorithm = Objects.requireNonNull(strongConnectivityAlgorithm, "strongConnectivityAlgorithm should not be null!");
        if (maximumIterations < 0) {
            throw new IllegalArgumentException("maximumIterations should be non-negative");
        }
        this.maximumIterations = maximumIterations;
        this.comparator = new ToleranceDoubleComparator(toleranceEpsilon);
        this.policyGraph = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        this.reachedVertices = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        this.vertexLevel = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        this.vertexDistance = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
    }

    @Override
    public double getCycleMean() {
        GraphPath<V, E> cycle = this.getCycle();
        if (cycle == null) {
            return Double.POSITIVE_INFINITY;
        }
        return cycle.getWeight() / (double)cycle.getLength();
    }

    @Override
    public GraphPath<V, E> getCycle() {
        boolean isBestCycleFound = false;
        double bestCycleWeight = 0.0;
        int bestCycleLength = 1;
        V bestCycleVertex = null;
        int numberOfIterations = 0;
        for (Graph<V, E> component : this.strongConnectivityAlgorithm.getStronglyConnectedComponents()) {
            boolean skip = component.vertexSet().size() == 0;
            if (skip |= component.vertexSet().size() == 1 && component.incomingEdgesOf(component.vertexSet().iterator().next()).size() == 0) continue;
            this.constructPolicyGraph(component);
            boolean improved = true;
            while (numberOfIterations < this.maximumIterations && improved) {
                this.constructCycle(component);
                improved = this.computeVertexDistance(component);
                ++numberOfIterations;
            }
            if (this.isCurrentCycleFound && (!isBestCycleFound || this.currentCycleWeight * (double)bestCycleLength < bestCycleWeight * (double)this.currentCycleLength)) {
                isBestCycleFound = true;
                bestCycleWeight = this.currentCycleWeight;
                bestCycleLength = this.currentCycleLength;
                bestCycleVertex = this.currentCycleVertex;
            }
            if (numberOfIterations != this.maximumIterations) continue;
            break;
        }
        if (isBestCycleFound) {
            return this.buildPath(bestCycleVertex, bestCycleLength, bestCycleWeight);
        }
        return null;
    }

    private void constructPolicyGraph(Graph<V, E> component) {
        for (V v : component.vertexSet()) {
            this.vertexDistance.put((Double)v, Double.POSITIVE_INFINITY);
        }
        for (V u : component.vertexSet()) {
            for (E e : component.incomingEdgesOf(u)) {
                V v = Graphs.getOppositeVertex(component, e, u);
                double eWeight = component.getEdgeWeight(e);
                if (!(eWeight < this.vertexDistance.get(v))) continue;
                this.vertexDistance.put((Double)v, eWeight);
                this.policyGraph.put(v, e);
            }
        }
    }

    private void constructCycle(Graph<V, E> component) {
        for (V v : component.vertexSet()) {
            this.vertexLevel.put((Integer)v, -1);
        }
        this.isCurrentCycleFound = false;
        int currentCycleLevel = 0;
        for (V u : component.vertexSet()) {
            if (this.vertexLevel.get(u) >= 0) continue;
            while (this.vertexLevel.get(u) < 0) {
                this.vertexLevel.put((Integer)u, currentCycleLevel);
                u = Graphs.getOppositeVertex(component, this.policyGraph.get(u), u);
            }
            if (this.vertexLevel.get(u) == currentCycleLevel) {
                double currentWeight = component.getEdgeWeight(this.policyGraph.get(u));
                int currentSize = 1;
                V v = Graphs.getOppositeVertex(component, this.policyGraph.get(u), u);
                while (!v.equals(u)) {
                    currentWeight += component.getEdgeWeight(this.policyGraph.get(v));
                    ++currentSize;
                    v = Graphs.getOppositeVertex(component, this.policyGraph.get(v), v);
                }
                if (!this.isCurrentCycleFound || currentWeight * (double)this.currentCycleLength < this.currentCycleWeight * (double)currentSize) {
                    this.isCurrentCycleFound = true;
                    this.currentCycleWeight = currentWeight;
                    this.currentCycleLength = currentSize;
                    this.currentCycleVertex = u;
                }
            }
            ++currentCycleLevel;
        }
    }

    private boolean computeVertexDistance(Graph<V, E> component) {
        ArrayDeque<V> queue = new ArrayDeque<V>();
        for (V v : component.vertexSet()) {
            this.reachedVertices.put((Boolean)v, false);
        }
        queue.addLast(this.currentCycleVertex);
        this.reachedVertices.put((Boolean)this.currentCycleVertex, true);
        double currentMean = this.currentCycleWeight / (double)this.currentCycleLength;
        while (!queue.isEmpty()) {
            Object u = queue.removeFirst();
            for (Object e : component.incomingEdgesOf(u)) {
                V v = Graphs.getOppositeVertex(component, e, u);
                if (!this.policyGraph.get(v).equals(e) || this.reachedVertices.get(v).booleanValue()) continue;
                this.reachedVertices.put((Boolean)v, true);
                double updatedDistance = this.vertexDistance.get(u) + component.getEdgeWeight(e) - currentMean;
                this.vertexDistance.put((Double)v, updatedDistance);
                queue.addLast(v);
            }
        }
        boolean improved = false;
        for (Object u : component.vertexSet()) {
            for (E e : component.incomingEdgesOf(u)) {
                double updatedDistance;
                Object v = Graphs.getOppositeVertex(component, e, u);
                double oldDistance = this.vertexDistance.get(v);
                if (!(oldDistance > (updatedDistance = this.vertexDistance.get(u) + component.getEdgeWeight(e) - currentMean))) continue;
                if (this.comparator.compare(oldDistance, updatedDistance) > 0) {
                    improved = true;
                }
                this.vertexDistance.put((Double)v, updatedDistance);
                this.policyGraph.put(v, e);
            }
        }
        return improved;
    }

    private GraphPath<V, E> buildPath(V bestCycleVertex, int bestCycleLength, double bestCycleWeight) {
        ArrayList<E> pathEdges = new ArrayList<E>(bestCycleLength);
        ArrayList<V> pathVertices = new ArrayList<V>(bestCycleLength + 1);
        V v = bestCycleVertex;
        pathVertices.add(bestCycleVertex);
        do {
            E e = this.policyGraph.get(v);
            v = Graphs.getOppositeVertex(this.graph, e, v);
            pathEdges.add(e);
            pathVertices.add(v);
        } while (!v.equals(bestCycleVertex));
        return new GraphWalk<V, E>(this.graph, bestCycleVertex, bestCycleVertex, pathVertices, pathEdges, bestCycleWeight);
    }
}

