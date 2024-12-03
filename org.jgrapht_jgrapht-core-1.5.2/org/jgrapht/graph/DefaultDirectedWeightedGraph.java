/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DefaultDirectedWeightedGraph<V, E>
extends DefaultDirectedGraph<V, E> {
    private static final long serialVersionUID = -4867672646995721544L;

    public DefaultDirectedWeightedGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public DefaultDirectedWeightedGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends DefaultDirectedWeightedGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DefaultDirectedWeightedGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DefaultDirectedWeightedGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DefaultDirectedWeightedGraph<V, E>(null, edgeSupplier));
    }
}

