/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class SimpleWeightedGraph<V, E>
extends SimpleGraph<V, E> {
    private static final long serialVersionUID = -1568410577378365671L;

    public SimpleWeightedGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public SimpleWeightedGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends SimpleWeightedGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new SimpleWeightedGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends SimpleWeightedGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new SimpleWeightedGraph<V, E>(null, edgeSupplier));
    }
}

