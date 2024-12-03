/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnorderedPair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleWeightedGraph;

public class ChinesePostman<V, E> {
    public GraphPath<V, E> getCPPSolution(Graph<V, E> graph) {
        GraphTests.requireDirectedOrUndirected(graph);
        if (graph.vertexSet().isEmpty() || graph.edgeSet().isEmpty()) {
            return new HierholzerEulerianCycle<V, E>().getEulerianCycle(graph);
        }
        assert (GraphTests.isStronglyConnected(graph));
        if (graph.getType().isUndirected()) {
            return this.solveCPPUndirected(graph);
        }
        return this.solveCPPDirected(graph);
    }

    private GraphPath<V, E> solveCPPUndirected(Graph<V, E> graph) {
        List oddDegreeVertices = graph.vertexSet().stream().filter(v -> graph.degreeOf(v) % 2 == 1).collect(Collectors.toList());
        HashMap shortestPaths = new HashMap();
        DijkstraShortestPath sp = new DijkstraShortestPath(graph);
        for (int i = 0; i < oddDegreeVertices.size() - 1; ++i) {
            Object u = oddDegreeVertices.get(i);
            ShortestPathAlgorithm.SingleSourcePaths paths = sp.getPaths(u);
            for (int j = i + 1; j < oddDegreeVertices.size(); ++j) {
                Object v2 = oddDegreeVertices.get(j);
                shortestPaths.put(new UnorderedPair(u, v2), paths.getPath(v2));
            }
        }
        SimpleWeightedGraph auxGraph = new SimpleWeightedGraph(DefaultWeightedEdge.class);
        Graphs.addAllVertices(auxGraph, oddDegreeVertices);
        for (Object u : oddDegreeVertices) {
            for (Object v2 : oddDegreeVertices) {
                if (u == v2) continue;
                Graphs.addEdge(auxGraph, u, v2, ((GraphPath)shortestPaths.get(new UnorderedPair(u, v2))).getWeight());
            }
        }
        MatchingAlgorithm.Matching matching = new KolmogorovWeightedPerfectMatching(auxGraph).getMatching();
        Pseudograph eulerGraph = new Pseudograph(graph.getVertexSupplier(), graph.getEdgeSupplier(), graph.getType().isWeighted());
        Graphs.addGraph(eulerGraph, graph);
        HashMap shortcutEdges = new HashMap();
        for (DefaultWeightedEdge e : matching.getEdges()) {
            Object u = auxGraph.getEdgeSource(e);
            Object v3 = auxGraph.getEdgeTarget(e);
            Object shortcutEdge = eulerGraph.addEdge(u, v3);
            shortcutEdges.put(shortcutEdge, (GraphPath)shortestPaths.get(new UnorderedPair(u, v3)));
        }
        HierholzerEulerianCycle eulerianCycleAlgorithm = new HierholzerEulerianCycle();
        GraphPath<V, E> pathWithShortcuts = eulerianCycleAlgorithm.getEulerianCycle(eulerGraph);
        return this.replaceShortcutEdges(graph, pathWithShortcuts, shortcutEdges);
    }

    private GraphPath<V, E> solveCPPDirected(Graph<V, E> graph) {
        int i;
        LinkedHashMap<V, Integer> imbalancedVertices = new LinkedHashMap<V, Integer>();
        HashSet<V> negImbalancedVertices = new HashSet<V>();
        HashSet<V> postImbalancedVertices = new HashSet<V>();
        for (V v : graph.vertexSet()) {
            int imbalance = graph.outDegreeOf(v) - graph.inDegreeOf(v);
            if (imbalance == 0) continue;
            imbalancedVertices.put(v, Math.abs(imbalance));
            if (imbalance < 0) {
                negImbalancedVertices.add(v);
                continue;
            }
            postImbalancedVertices.add(v);
        }
        HashMap shortestPaths = new HashMap();
        DijkstraShortestPath sp = new DijkstraShortestPath(graph);
        for (Object u : negImbalancedVertices) {
            ShortestPathAlgorithm.SingleSourcePaths paths = sp.getPaths(u);
            for (Object v : postImbalancedVertices) {
                shortestPaths.put(new Pair(u, v), paths.getPath(v));
            }
        }
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> auxGraph = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        ArrayList duplicateMap = new ArrayList();
        HashSet<Integer> negImbalancedPartition = new HashSet<Integer>();
        HashSet<Integer> postImbalancedPartition = new HashSet<Integer>();
        Integer vertex = 0;
        for (Object v : negImbalancedVertices) {
            for (i = 0; i < (Integer)imbalancedVertices.get(v); ++i) {
                auxGraph.addVertex(vertex);
                duplicateMap.add(v);
                negImbalancedPartition.add(vertex);
                Integer n = vertex;
                vertex = vertex + 1;
            }
        }
        for (Object v : postImbalancedVertices) {
            for (i = 0; i < (Integer)imbalancedVertices.get(v); ++i) {
                auxGraph.addVertex(vertex);
                duplicateMap.add(v);
                postImbalancedPartition.add(vertex);
                Integer n = vertex;
                vertex = vertex + 1;
            }
        }
        for (Integer i2 : negImbalancedPartition) {
            for (Integer n : postImbalancedPartition) {
                Object u = duplicateMap.get(i2);
                Object v = duplicateMap.get(n);
                Graphs.addEdge(auxGraph, i2, n, ((GraphPath)shortestPaths.get(new Pair(u, v))).getWeight());
            }
        }
        MatchingAlgorithm.Matching matching = new KuhnMunkresMinimalWeightBipartitePerfectMatching(auxGraph, negImbalancedPartition, postImbalancedPartition).getMatching();
        DirectedPseudograph eulerGraph = new DirectedPseudograph(graph.getVertexSupplier(), graph.getEdgeSupplier(), graph.getType().isWeighted());
        Graphs.addGraph(eulerGraph, graph);
        HashMap shortcutEdges = new HashMap();
        for (DefaultWeightedEdge e : matching.getEdges()) {
            int i3 = (Integer)auxGraph.getEdgeSource(e);
            int j = (Integer)auxGraph.getEdgeTarget(e);
            Object u = duplicateMap.get(i3);
            Object v = duplicateMap.get(j);
            Object shortcutEdge = eulerGraph.addEdge(u, v);
            shortcutEdges.put(shortcutEdge, (GraphPath)shortestPaths.get(new Pair(u, v)));
        }
        HierholzerEulerianCycle hierholzerEulerianCycle = new HierholzerEulerianCycle();
        GraphPath<V, E> pathWithShortcuts = hierholzerEulerianCycle.getEulerianCycle(eulerGraph);
        return this.replaceShortcutEdges(graph, pathWithShortcuts, shortcutEdges);
    }

    private GraphPath<V, E> replaceShortcutEdges(Graph<V, E> inputGraph, GraphPath<V, E> pathWithShortcuts, Map<E, GraphPath<V, E>> shortcutEdges) {
        V startVertex = pathWithShortcuts.getStartVertex();
        V endVertex = pathWithShortcuts.getEndVertex();
        ArrayList<V> vertexList = new ArrayList<V>();
        ArrayList<E> edgeList = new ArrayList<E>();
        List<V> verticesInPathWithShortcuts = pathWithShortcuts.getVertexList();
        List<E> edgesInPathWithShortcuts = pathWithShortcuts.getEdgeList();
        for (int i = 0; i < verticesInPathWithShortcuts.size() - 1; ++i) {
            vertexList.add(verticesInPathWithShortcuts.get(i));
            E edge = edgesInPathWithShortcuts.get(i);
            if (shortcutEdges.containsKey(edge)) {
                GraphPath<V, E> shortcut = shortcutEdges.get(edge);
                if (vertexList.get(vertexList.size() - 1).equals(shortcut.getStartVertex())) {
                    vertexList.addAll(shortcut.getVertexList().subList(1, shortcut.getVertexList().size() - 1));
                    edgeList.addAll(shortcut.getEdgeList());
                    continue;
                }
                ArrayList<V> reverseVertices = new ArrayList<V>(shortcut.getVertexList().subList(1, shortcut.getVertexList().size() - 1));
                Collections.reverse(reverseVertices);
                ArrayList<E> reverseEdges = new ArrayList<E>(shortcut.getEdgeList());
                Collections.reverse(reverseEdges);
                vertexList.addAll(reverseVertices);
                edgeList.addAll(reverseEdges);
                continue;
            }
            edgeList.add(edge);
        }
        vertexList.add(endVertex);
        double pathWeight = edgeList.stream().mapToDouble(inputGraph::getEdgeWeight).sum();
        return new GraphWalk<V, E>(inputGraph, startVertex, endVertex, vertexList, edgeList, pathWeight);
    }
}

