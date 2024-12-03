/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.util.UnorderedPair;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.GraphWalk;

abstract class BaseKDisjointShortestPathsAlgorithm<V, E>
implements KShortestPathAlgorithm<V, E> {
    protected Graph<V, E> workingGraph;
    protected List<List<E>> pathList;
    protected Graph<V, E> originalGraph;
    private Set<E> validEdges;

    public BaseKDisjointShortestPathsAlgorithm(Graph<V, E> graph) {
        this.originalGraph = graph;
        GraphTests.requireDirected(graph);
        if (!GraphTests.isSimple(graph)) {
            throw new IllegalArgumentException("Graph must be simple");
        }
    }

    @Override
    public List<GraphPath<V, E>> getPaths(V startVertex, V endVertex, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("Number of paths must be positive");
        }
        Objects.requireNonNull(startVertex, "startVertex is null");
        Objects.requireNonNull(endVertex, "endVertex is null");
        if (endVertex.equals(startVertex)) {
            throw new IllegalArgumentException("The end vertex is the same as the start vertex!");
        }
        if (!this.originalGraph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("graph must contain the start vertex!");
        }
        if (!this.originalGraph.containsVertex(endVertex)) {
            throw new IllegalArgumentException("graph must contain the end vertex!");
        }
        this.workingGraph = new AsWeightedGraph<V, E>(new DefaultDirectedWeightedGraph<V, E>(this.originalGraph.getVertexSupplier(), this.originalGraph.getEdgeSupplier()), new HashMap(), false);
        Graphs.addGraph(this.workingGraph, this.originalGraph);
        this.pathList = new ArrayList<List<E>>();
        GraphPath<V, E> currentPath = this.calculateShortestPath(startVertex, endVertex);
        if (currentPath != null) {
            this.pathList.add(currentPath.getEdgeList());
            for (int i = 0; i < k - 1; ++i) {
                this.transformGraph(this.pathList.get(i));
                currentPath = this.calculateShortestPath(startVertex, endVertex);
                if (currentPath == null) break;
                this.pathList.add(currentPath.getEdgeList());
            }
        }
        return this.pathList.size() > 0 ? this.resolvePaths(startVertex, endVertex) : Collections.emptyList();
    }

    private List<GraphPath<V, E>> resolvePaths(V startVertex, V endVertex) {
        this.findValidEdges();
        List<GraphPath<V, E>> paths = this.buildPaths(startVertex, endVertex);
        Collections.sort(paths, Comparator.comparingDouble(GraphPath::getWeight));
        return paths;
    }

    private List<GraphPath<V, E>> buildPaths(V startVertex, V endVertex) {
        Map<Object, ArrayDeque> sourceVertexToEdge = this.validEdges.stream().collect(Collectors.groupingBy(this::getEdgeSource, Collectors.toCollection(ArrayDeque::new)));
        ArrayDeque startEdges = sourceVertexToEdge.get(startVertex);
        ArrayList result = new ArrayList();
        for (Object edge : startEdges) {
            V edgeTarget;
            ArrayList resultPath = new ArrayList();
            resultPath.add(edge);
            while (!(edgeTarget = this.getEdgeTarget(edge)).equals(endVertex)) {
                ArrayDeque outgoingEdges = sourceVertexToEdge.get(edgeTarget);
                edge = outgoingEdges.poll();
                resultPath.add(edge);
            }
            GraphPath graphPath = this.createGraphPath(resultPath, startVertex, endVertex);
            result.add(graphPath);
        }
        return result;
    }

    private void findValidEdges() {
        LinkedHashMap<UnorderedPair, Object> validEdges = new LinkedHashMap<UnorderedPair, Object>();
        for (List<E> path : this.pathList) {
            for (Object e : path) {
                V v = this.getEdgeSource(e);
                V u = this.getEdgeTarget(e);
                UnorderedPair<V, V> edgePair = new UnorderedPair<V, V>(v, u);
                validEdges.compute(edgePair, (unused, edge) -> edge == null ? e : null);
            }
        }
        this.validEdges = new LinkedHashSet(validEdges.values());
    }

    private GraphPath<V, E> createGraphPath(List<E> edgeList, V startVertex, V endVertex) {
        double weight = 0.0;
        for (E edge : edgeList) {
            weight += this.originalGraph.getEdgeWeight(edge);
        }
        return new GraphWalk<V, E>(this.originalGraph, startVertex, endVertex, edgeList, weight);
    }

    private V getEdgeSource(E e) {
        return this.workingGraph.containsEdge(e) ? this.workingGraph.getEdgeSource(e) : this.originalGraph.getEdgeSource(e);
    }

    private V getEdgeTarget(E e) {
        return this.workingGraph.containsEdge(e) ? this.workingGraph.getEdgeTarget(e) : this.originalGraph.getEdgeTarget(e);
    }

    protected abstract GraphPath<V, E> calculateShortestPath(V var1, V var2);

    protected abstract void transformGraph(List<E> var1);
}

