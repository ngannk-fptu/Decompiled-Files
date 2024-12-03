/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.transform;

import java.util.Objects;
import java.util.function.BiFunction;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

public class LineGraphConverter<V, E, EE> {
    private final Graph<V, E> graph;

    public LineGraphConverter(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    public void convertToLineGraph(Graph<E, EE> target) {
        this.convertToLineGraph(target, null);
    }

    public void convertToLineGraph(Graph<E, EE> target, BiFunction<E, E, Double> weightFunction) {
        Graphs.addAllVertices(target, this.graph.edgeSet());
        if (this.graph.getType().isDirected()) {
            for (V vertex : this.graph.vertexSet()) {
                for (E e1 : this.graph.incomingEdgesOf(vertex)) {
                    for (E e2 : this.graph.outgoingEdgesOf(vertex)) {
                        EE edge = target.addEdge(e1, e2);
                        if (weightFunction == null) continue;
                        target.setEdgeWeight(edge, weightFunction.apply(e1, e2));
                    }
                }
            }
        } else {
            for (V v : this.graph.vertexSet()) {
                for (E e1 : this.graph.edgesOf(v)) {
                    for (E e2 : this.graph.edgesOf(v)) {
                        if (e1 == e2) continue;
                        EE edge = target.addEdge(e1, e2);
                        if (weightFunction == null) continue;
                        target.setEdgeWeight(edge, weightFunction.apply(e1, e2));
                    }
                }
            }
        }
    }
}

