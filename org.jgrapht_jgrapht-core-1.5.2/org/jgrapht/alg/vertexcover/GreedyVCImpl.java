/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.vertexcover;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.vertexcover.util.RatioVertex;

public class GreedyVCImpl<V, E>
implements VertexCoverAlgorithm<V> {
    private static int vertexCounter = 0;
    private final Graph<V, E> graph;
    private final Map<V, Double> vertexWeightMap;

    public GreedyVCImpl(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
        this.vertexWeightMap = graph.vertexSet().stream().collect(Collectors.toMap(Function.identity(), vertex -> 1.0));
    }

    public GreedyVCImpl(Graph<V, E> graph, Map<V, Double> vertexWeightMap) {
        this.graph = GraphTests.requireUndirected(graph);
        this.vertexWeightMap = Objects.requireNonNull(vertexWeightMap);
    }

    @Override
    public VertexCoverAlgorithm.VertexCover<V> getVertexCover() {
        LinkedHashSet cover = new LinkedHashSet();
        double weight = 0.0;
        HashMap vertexEncapsulationMap = new HashMap();
        this.graph.vertexSet().stream().filter(v -> this.graph.degreeOf(v) > 0).forEach(v -> vertexEncapsulationMap.put(v, new RatioVertex<Object>(vertexCounter++, v, this.vertexWeightMap.get(v))));
        for (E e : this.graph.edgeSet()) {
            V u = this.graph.getEdgeSource(e);
            RatioVertex ux2 = (RatioVertex)vertexEncapsulationMap.get(u);
            V v2 = this.graph.getEdgeTarget(e);
            RatioVertex vx = (RatioVertex)vertexEncapsulationMap.get(v2);
            ux2.addNeighbor(vx);
            vx.addNeighbor(ux2);
            assert (ux2.neighbors.get(vx).intValue() == vx.neighbors.get(ux2).intValue()) : " in an undirected graph, if vx is a neighbor of ux, then ux must be a neighbor of vx";
        }
        TreeSet<Object> workingGraph = new TreeSet<Object>();
        workingGraph.addAll(vertexEncapsulationMap.values());
        assert (workingGraph.size() == vertexEncapsulationMap.size()) : "vertices in vertexEncapsulationMap: " + this.graph.vertexSet().size() + "vertices in working graph: " + workingGraph.size();
        while (!workingGraph.isEmpty()) {
            RatioVertex vx = (RatioVertex)workingGraph.pollFirst();
            assert (workingGraph.parallelStream().allMatch(ux -> vx.getRatio() <= ux.getRatio())) : "vx does not have the smallest ratio among all elements. VX: " + vx + " WorkingGraph: " + workingGraph;
            for (RatioVertex nx : vx.neighbors.keySet()) {
                if (nx == vx) continue;
                workingGraph.remove(nx);
                nx.removeNeighbor(vx);
                if (nx.getDegree() <= 0) continue;
                workingGraph.add(nx);
            }
            cover.add(vx.v);
            weight += this.vertexWeightMap.get(vx.v).doubleValue();
            assert (workingGraph.parallelStream().noneMatch(ux -> ux.id == vx.id)) : "vx should no longer exist in the working graph";
        }
        return new VertexCoverAlgorithm.VertexCoverImpl(cover, weight);
    }
}

