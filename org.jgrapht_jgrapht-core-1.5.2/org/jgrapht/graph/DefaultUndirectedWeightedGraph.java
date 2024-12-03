/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DefaultUndirectedWeightedGraph<V, E>
extends DefaultUndirectedGraph<V, E> {
    private static final long serialVersionUID = -1008165881690129042L;

    public DefaultUndirectedWeightedGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public DefaultUndirectedWeightedGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends DefaultUndirectedWeightedGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DefaultUndirectedWeightedGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DefaultUndirectedWeightedGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DefaultUndirectedWeightedGraph<V, E>(null, edgeSupplier));
    }
}

