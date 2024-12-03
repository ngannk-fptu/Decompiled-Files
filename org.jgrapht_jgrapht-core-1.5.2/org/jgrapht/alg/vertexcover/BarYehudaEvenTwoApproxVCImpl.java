/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.vertexcover;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.graph.AsSubgraph;

public class BarYehudaEvenTwoApproxVCImpl<V, E>
implements VertexCoverAlgorithm<V> {
    private final Graph<V, E> graph;
    private final Map<V, Double> vertexWeightMap;

    public BarYehudaEvenTwoApproxVCImpl(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
        this.vertexWeightMap = graph.vertexSet().stream().collect(Collectors.toMap(Function.identity(), vertex -> 1.0));
    }

    public BarYehudaEvenTwoApproxVCImpl(Graph<V, E> graph, Map<V, Double> vertexWeightMap) {
        this.graph = GraphTests.requireUndirected(graph);
        this.vertexWeightMap = Objects.requireNonNull(vertexWeightMap);
    }

    @Override
    public VertexCoverAlgorithm.VertexCover<V> getVertexCover() {
        LinkedHashSet cover = new LinkedHashSet();
        double weight = 0.0;
        AsSubgraph copy = new AsSubgraph(this.graph, null, null);
        HashMap w = new HashMap();
        for (V v : this.graph.vertexSet()) {
            w.put(v, this.vertexWeightMap.get(v));
        }
        Set edgeSet = copy.edgeSet();
        while (!edgeSet.isEmpty()) {
            Object e = edgeSet.iterator().next();
            Object p = copy.getEdgeSource(e);
            Object q = copy.getEdgeTarget(e);
            if ((Double)w.get(p) <= (Double)w.get(q)) {
                w.put(q, (Double)w.get(q) - (Double)w.get(p));
                cover.add(p);
                weight += this.vertexWeightMap.get(p).doubleValue();
                copy.removeVertex(p);
                continue;
            }
            w.put(p, (Double)w.get(p) - (Double)w.get(q));
            cover.add(q);
            weight += this.vertexWeightMap.get(q).doubleValue();
            copy.removeVertex(q);
        }
        return new VertexCoverAlgorithm.VertexCoverImpl(cover, weight);
    }
}

