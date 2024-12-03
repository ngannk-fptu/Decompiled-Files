/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.util.CollectionUtil;

public class ChordalGraphMinimalVertexSeparatorFinder<V, E> {
    private final Graph<V, E> graph;
    private final ChordalityInspector<V, E> chordalityInspector;
    private Map<Set<V>, Integer> minimalSeparatorsWithMultiplicities;

    public ChordalGraphMinimalVertexSeparatorFinder(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        this.chordalityInspector = new ChordalityInspector<V, E>(graph, ChordalityInspector.IterationOrder.MCS);
    }

    public Set<Set<V>> getMinimalSeparators() {
        this.lazyComputeMinimalSeparatorsWithMultiplicities();
        return this.minimalSeparatorsWithMultiplicities == null ? null : this.minimalSeparatorsWithMultiplicities.keySet();
    }

    public Map<Set<V>, Integer> getMinimalSeparatorsWithMultiplicities() {
        this.lazyComputeMinimalSeparatorsWithMultiplicities();
        return this.minimalSeparatorsWithMultiplicities;
    }

    private void lazyComputeMinimalSeparatorsWithMultiplicities() {
        if (this.minimalSeparatorsWithMultiplicities == null && this.chordalityInspector.isChordal()) {
            this.minimalSeparatorsWithMultiplicities = new HashMap<Set<V>, Integer>();
            List<V> perfectEliminationOrder = this.chordalityInspector.getPerfectEliminationOrder();
            Map<V, Integer> vertexInOrder = this.getVertexInOrder(perfectEliminationOrder);
            Set<Object> current = new HashSet();
            for (int i = 1; i < perfectEliminationOrder.size(); ++i) {
                HashSet previous = current;
                current = this.getPredecessors(vertexInOrder, perfectEliminationOrder.get(i));
                if (current.size() > previous.size()) continue;
                if (this.minimalSeparatorsWithMultiplicities.containsKey(current)) {
                    this.minimalSeparatorsWithMultiplicities.put(current, this.minimalSeparatorsWithMultiplicities.get(current) + 1);
                    continue;
                }
                this.minimalSeparatorsWithMultiplicities.put(current, 1);
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
}

