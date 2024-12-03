/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class SimpleGraph<V, E>
extends AbstractBaseGraph<V, E> {
    private static final long serialVersionUID = 4607246833824317836L;

    public SimpleGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false);
    }

    public SimpleGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().undirected().allowMultipleEdges(false).allowSelfLoops(false).weighted(weighted).build());
    }

    public static <V, E> GraphBuilder<V, E, ? extends SimpleGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new SimpleGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends SimpleGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new SimpleGraph<V, E>(null, edgeSupplier, false));
    }
}

