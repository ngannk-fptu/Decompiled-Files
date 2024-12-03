/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DirectedMultigraph<V, E>
extends AbstractBaseGraph<V, E> {
    private static final long serialVersionUID = 2919338637676573948L;

    public DirectedMultigraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false);
    }

    public DirectedMultigraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().directed().allowMultipleEdges(true).allowSelfLoops(false).weighted(weighted).build());
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedMultigraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DirectedMultigraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedMultigraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DirectedMultigraph<V, E>(null, edgeSupplier, false));
    }
}

