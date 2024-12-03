/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class SimpleDirectedWeightedGraph<V, E>
extends SimpleDirectedGraph<V, E> {
    private static final long serialVersionUID = -3301373580757772501L;

    public SimpleDirectedWeightedGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public SimpleDirectedWeightedGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends SimpleDirectedWeightedGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new SimpleDirectedWeightedGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends SimpleDirectedWeightedGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new SimpleDirectedWeightedGraph<V, E>(null, edgeSupplier));
    }
}

