/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.GraphSpecificsStrategy;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;
import org.jgrapht.graph.UniformIntrusiveEdgesSpecifics;
import org.jgrapht.graph.WeightedIntrusiveEdgesSpecifics;
import org.jgrapht.graph.specifics.DirectedSpecifics;
import org.jgrapht.graph.specifics.Specifics;
import org.jgrapht.graph.specifics.UndirectedSpecifics;

public class DefaultGraphSpecificsStrategy<V, E>
implements GraphSpecificsStrategy<V, E> {
    private static final long serialVersionUID = 7615319421753562075L;

    @Override
    public Function<GraphType, IntrusiveEdgesSpecifics<V, E>> getIntrusiveEdgesSpecificsFactory() {
        return (Function<GraphType, IntrusiveEdgesSpecifics> & Serializable)type -> {
            if (type.isWeighted()) {
                return new WeightedIntrusiveEdgesSpecifics(new LinkedHashMap());
            }
            return new UniformIntrusiveEdgesSpecifics(new LinkedHashMap());
        };
    }

    @Override
    public BiFunction<Graph<V, E>, GraphType, Specifics<V, E>> getSpecificsFactory() {
        return (BiFunction<Graph, GraphType, Specifics> & Serializable)(graph, type) -> {
            if (type.isDirected()) {
                return new DirectedSpecifics(graph, new LinkedHashMap(), this.getEdgeSetFactory());
            }
            return new UndirectedSpecifics(graph, new LinkedHashMap(), this.getEdgeSetFactory());
        };
    }
}

