/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.GraphWalk;

public class AllDirectedPaths<V, E> {
    private final Graph<V, E> graph;
    private final PathValidator<V, E> pathValidator;

    public AllDirectedPaths(Graph<V, E> graph) {
        this(graph, null);
    }

    public AllDirectedPaths(Graph<V, E> graph, PathValidator<V, E> pathValidator) {
        this.graph = GraphTests.requireDirected(graph);
        this.pathValidator = pathValidator;
    }

    public List<GraphPath<V, E>> getAllPaths(V sourceVertex, V targetVertex, boolean simplePathsOnly, Integer maxPathLength) {
        return this.getAllPaths(Collections.singleton(sourceVertex), Collections.singleton(targetVertex), simplePathsOnly, maxPathLength);
    }

    public List<GraphPath<V, E>> getAllPaths(Set<V> sourceVertices, Set<V> targetVertices, boolean simplePathsOnly, Integer maxPathLength) {
        if (maxPathLength != null && maxPathLength < 0) {
            throw new IllegalArgumentException("maxPathLength must be non-negative if defined");
        }
        if (!simplePathsOnly && maxPathLength == null) {
            throw new IllegalArgumentException("If search is not restricted to simple paths, a maximum path length must be set to avoid infinite cycles");
        }
        if (sourceVertices.isEmpty() || targetVertices.isEmpty()) {
            return Collections.emptyList();
        }
        Map<E, Integer> edgeMinDistancesFromTargets = this.edgeMinDistancesBackwards(targetVertices, maxPathLength);
        return this.generatePaths(sourceVertices, targetVertices, simplePathsOnly, maxPathLength, edgeMinDistancesFromTargets);
    }

    private Map<E, Integer> edgeMinDistancesBackwards(Set<V> targetVertices, Integer maxPathLength) {
        Object vertex;
        HashMap<E, Integer> edgeMinDistances = new HashMap<E, Integer>();
        HashMap<V, Integer> vertexMinDistances = new HashMap<V, Integer>();
        ArrayDeque<V> verticesToProcess = new ArrayDeque<V>();
        if (maxPathLength != null) {
            if (maxPathLength < 0) {
                throw new IllegalArgumentException("maxPathLength must be non-negative if defined");
            }
            if (maxPathLength == 0) {
                return edgeMinDistances;
            }
        }
        for (V target : targetVertices) {
            vertexMinDistances.put(target, 0);
            verticesToProcess.add(target);
        }
        while ((vertex = verticesToProcess.poll()) != null) {
            assert (vertexMinDistances.containsKey(vertex));
            Integer childDistance = (Integer)vertexMinDistances.get(vertex) + 1;
            for (E edge : this.graph.incomingEdgesOf(vertex)) {
                V edgeSource;
                if (!edgeMinDistances.containsKey(edge) || (Integer)edgeMinDistances.get(edge) > childDistance) {
                    edgeMinDistances.put(edge, childDistance);
                }
                if (vertexMinDistances.containsKey(edgeSource = this.graph.getEdgeSource(edge)) && (Integer)vertexMinDistances.get(edgeSource) <= childDistance) continue;
                vertexMinDistances.put(edgeSource, childDistance);
                if (maxPathLength != null && childDistance >= maxPathLength) continue;
                verticesToProcess.add(edgeSource);
            }
        }
        assert (verticesToProcess.isEmpty());
        return edgeMinDistances;
    }

    private List<GraphPath<V, E>> generatePaths(Set<V> sourceVertices, Set<V> targetVertices, boolean simplePathsOnly, Integer maxPathLength, Map<E, Integer> edgeMinDistancesFromTargets) {
        List incompletePath;
        ArrayList completePaths = new ArrayList();
        LinkedList<List<E>> incompletePaths = new LinkedList<List<E>>();
        if (maxPathLength != null && maxPathLength < 0) {
            throw new IllegalArgumentException("maxPathLength must be non-negative if defined");
        }
        for (V source : sourceVertices) {
            if (targetVertices.contains(source)) {
                completePaths.add(GraphWalk.singletonWalk(this.graph, source, 0.0));
            }
            if (maxPathLength != null && maxPathLength == 0) continue;
            for (E edge : this.graph.outgoingEdgesOf(source)) {
                assert (this.graph.getEdgeSource(edge).equals(source));
                if (this.pathValidator != null && !this.pathValidator.isValidPath(GraphWalk.emptyWalk(this.graph), edge)) continue;
                if (targetVertices.contains(this.graph.getEdgeTarget(edge))) {
                    completePaths.add(this.makePath(Collections.singletonList(edge)));
                }
                if (!edgeMinDistancesFromTargets.containsKey(edge) || maxPathLength != null && maxPathLength <= 1) continue;
                List<E> path = Collections.singletonList(edge);
                incompletePaths.add(path);
            }
        }
        if (maxPathLength != null && maxPathLength == 0) {
            return completePaths;
        }
        while ((incompletePath = (List)incompletePaths.poll()) != null) {
            Integer lengthSoFar = incompletePath.size();
            assert (maxPathLength == null || lengthSoFar < maxPathLength);
            Object leafEdge = incompletePath.get(lengthSoFar - 1);
            V leafNode = this.graph.getEdgeTarget(leafEdge);
            HashSet<V> pathVertices = new HashSet<V>();
            for (Object pathEdge : incompletePath) {
                pathVertices.add(this.graph.getEdgeSource(pathEdge));
                pathVertices.add(this.graph.getEdgeTarget(pathEdge));
            }
            for (Object outEdge : this.graph.outgoingEdgesOf(leafNode)) {
                if (!edgeMinDistancesFromTargets.containsKey(outEdge) || maxPathLength != null && edgeMinDistancesFromTargets.get(outEdge) + lengthSoFar > maxPathLength) continue;
                ArrayList newPath = new ArrayList(incompletePath);
                newPath.add(outEdge);
                if (simplePathsOnly && pathVertices.contains(this.graph.getEdgeTarget(outEdge)) || this.pathValidator != null && !this.pathValidator.isValidPath(this.makePath(incompletePath), outEdge)) continue;
                if (targetVertices.contains(this.graph.getEdgeTarget(outEdge))) {
                    GraphPath completePath = this.makePath(newPath);
                    assert (sourceVertices.contains(completePath.getStartVertex()));
                    assert (targetVertices.contains(completePath.getEndVertex()));
                    assert (maxPathLength == null || completePath.getLength() <= maxPathLength);
                    completePaths.add(completePath);
                }
                if (maxPathLength != null && newPath.size() >= maxPathLength) continue;
                incompletePaths.addFirst(newPath);
            }
        }
        assert (incompletePaths.isEmpty());
        return completePaths;
    }

    private GraphPath<V, E> makePath(List<E> edges) {
        V source = this.graph.getEdgeSource(edges.get(0));
        V target = this.graph.getEdgeTarget(edges.get(edges.size() - 1));
        double weight = edges.stream().mapToDouble(edge -> this.graph.getEdgeWeight(edge)).sum();
        return new GraphWalk<V, E>(this.graph, source, target, edges, weight);
    }
}

