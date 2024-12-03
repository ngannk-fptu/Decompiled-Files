/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.util.CollectionUtil;

public class ChristofidesThreeHalvesApproxMetricTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        int n = graph.vertexSet().size();
        if (n == 1) {
            return this.getSingletonTour(graph);
        }
        Pseudograph mstAndMatching = new Pseudograph(null, DefaultEdge::new, false);
        graph.vertexSet().forEach(mstAndMatching::addVertex);
        KruskalMinimumSpanningTree<V, E> spanningTreeAlgorithm = new KruskalMinimumSpanningTree<V, E>(graph);
        spanningTreeAlgorithm.getSpanningTree().getEdges().forEach(e -> mstAndMatching.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e)));
        Set oddDegreeVertices = mstAndMatching.vertexSet().stream().filter(v -> (mstAndMatching.edgesOf(v).size() & 1) == 1).collect(Collectors.toSet());
        AsSubgraph<V, E> subgraph = new AsSubgraph<V, E>(graph, oddDegreeVertices);
        KolmogorovWeightedPerfectMatching<V, E> matchingAlgorithm = new KolmogorovWeightedPerfectMatching<V, E>(subgraph);
        matchingAlgorithm.getMatching().getEdges().forEach(e -> mstAndMatching.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e)));
        HierholzerEulerianCycle eulerianCycleAlgorithm = new HierholzerEulerianCycle();
        GraphPath eulerianCycle = eulerianCycleAlgorithm.getEulerianCycle(mstAndMatching);
        HashSet visited = CollectionUtil.newHashSetWithExpectedSize(n);
        List tourVertices = eulerianCycle.getVertexList().stream().filter(visited::add).collect(Collectors.toList());
        return this.vertexListToTour(tourVertices, graph);
    }
}

