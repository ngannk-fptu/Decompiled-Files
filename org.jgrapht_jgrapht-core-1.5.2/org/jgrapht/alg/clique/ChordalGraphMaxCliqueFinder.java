/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clique;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.ChordalGraphColoring;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.interfaces.CliqueAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.util.CollectionUtil;

public class ChordalGraphMaxCliqueFinder<V, E>
implements CliqueAlgorithm<V> {
    private final Graph<V, E> graph;
    private final ChordalityInspector.IterationOrder iterationOrder;
    private CliqueAlgorithm.Clique<V> maximumClique;
    private boolean isChordal = true;

    public ChordalGraphMaxCliqueFinder(Graph<V, E> graph) {
        this(graph, ChordalityInspector.IterationOrder.MCS);
    }

    public ChordalGraphMaxCliqueFinder(Graph<V, E> graph, ChordalityInspector.IterationOrder iterationOrder) {
        this.graph = Objects.requireNonNull(graph);
        this.iterationOrder = Objects.requireNonNull(iterationOrder);
    }

    private void lazyComputeMaximumClique() {
        if (this.maximumClique == null && this.isChordal) {
            ChordalGraphColoring<V, E> cgc = new ChordalGraphColoring<V, E>(this.graph, this.iterationOrder);
            VertexColoringAlgorithm.Coloring<V> coloring = cgc.getColoring();
            List<V> perfectEliminationOrder = cgc.getPerfectEliminationOrder();
            if (coloring == null) {
                this.isChordal = false;
                return;
            }
            Map<V, Integer> vertexInOrder = this.getVertexInOrder(perfectEliminationOrder);
            Map.Entry maxEntry = coloring.getColors().entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).orElse(null);
            if (maxEntry == null) {
                this.maximumClique = new CliqueAlgorithm.CliqueImpl(Collections.emptySet());
            } else {
                Set<V> cliqueSet = this.getPredecessors(vertexInOrder, maxEntry.getKey());
                cliqueSet.add(maxEntry.getKey());
                this.maximumClique = new CliqueAlgorithm.CliqueImpl<V>(cliqueSet);
            }
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
    public CliqueAlgorithm.Clique<V> getClique() {
        this.lazyComputeMaximumClique();
        return this.maximumClique;
    }
}

