/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DefaultDirectedGraph<V, E>
extends AbstractBaseGraph<V, E> {
    private static final long serialVersionUID = -2066644490824847621L;

    public DefaultDirectedGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false);
    }

    public DefaultDirectedGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().directed().allowMultipleEdges(false).allowSelfLoops(true).weighted(weighted).build());
    }

    public static <V, E> GraphBuilder<V, E, ? extends DefaultDirectedGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DefaultDirectedGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DefaultDirectedGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DefaultDirectedGraph<V, E>(null, edgeSupplier, false));
    }
}

