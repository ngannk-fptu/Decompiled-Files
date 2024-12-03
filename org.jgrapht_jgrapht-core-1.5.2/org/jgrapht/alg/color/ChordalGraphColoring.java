/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.util.CollectionUtil;

public class ChordalGraphColoring<V, E>
implements VertexColoringAlgorithm<V> {
    private final Graph<V, E> graph;
    private final ChordalityInspector<V, E> chordalityInspector;
    private VertexColoringAlgorithm.Coloring<V> coloring;

    public ChordalGraphColoring(Graph<V, E> graph) {
        this(graph, ChordalityInspector.IterationOrder.MCS);
    }

    public ChordalGraphColoring(Graph<V, E> graph, ChordalityInspector.IterationOrder iterationOrder) {
        this.graph = Objects.requireNonNull(graph);
        this.chordalityInspector = new ChordalityInspector<V, E>(graph, iterationOrder);
    }

    private void lazyComputeColoring() {
        if (this.coloring == null && this.chordalityInspector.isChordal()) {
            List<V> perfectEliminationOrder = this.chordalityInspector.getPerfectEliminationOrder();
            HashMap vertexColoring = CollectionUtil.newHashMapWithExpectedSize(perfectEliminationOrder.size());
            Map<V, Integer> vertexInOrder = this.getVertexInOrder(perfectEliminationOrder);
            for (V vertex : perfectEliminationOrder) {
                Set<V> predecessors = this.getPredecessors(vertexInOrder, vertex);
                HashSet predecessorColors = CollectionUtil.newHashSetWithExpectedSize(predecessors.size());
                predecessors.forEach(v -> predecessorColors.add((Integer)vertexColoring.get(v)));
                int minUnusedColor = 0;
                while (predecessorColors.contains(minUnusedColor)) {
                    ++minUnusedColor;
                }
                vertexColoring.put(vertex, minUnusedColor);
            }
            int maxColor = (int)vertexColoring.values().stream().distinct().count();
            this.coloring = new VertexColoringAlgorithm.ColoringImpl(vertexColoring, maxColor);
        }
    }

    private Map<V, Integer> getVertexInOrder(List<V> vertexOrder) {
        HashMap<V, Integer> vertexInOrder = CollectionUtil.newHashMapWithExpectedSize(vertexOrder.size());
        int i = 0;
        for (V vertex : vertexOrder) {
            vertexInOrder.put(vertex, i++);
        }
        return vertexInOrder;
    }

    private Set<V> getPredecessors(Map<V, Integer> vertexInOrder, V vertex) {
        HashSet<V> predecessors = new HashSet<V>();
        Integer vertexPosition = vertexInOrder.get(vertex);
        Set<E> edges = this.graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(this.graph, edge, vertex);
            Integer destPosition = vertexInOrder.get(oppositeVertex);
            if (destPosition >= vertexPosition) continue;
            predecessors.add(oppositeVertex);
        }
        return predecessors;
    }

    @Override
    public VertexColoringAlgorithm.Coloring<V> getColoring() {
        this.lazyComputeColoring();
        return this.coloring;
    }

    public List<V> getPerfectEliminationOrder() {
        return this.chordalityInspector.getPerfectEliminationOrder();
    }
}

