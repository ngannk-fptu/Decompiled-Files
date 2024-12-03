/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class Pseudograph<V, E>
extends AbstractBaseGraph<V, E> {
    private static final long serialVersionUID = -7574564204896552581L;

    public Pseudograph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false);
    }

    public Pseudograph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().undirected().allowMultipleEdges(true).allowSelfLoops(true).weighted(weighted).build());
    }

    public static <V, E> GraphBuilder<V, E, ? extends Pseudograph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new Pseudograph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends Pseudograph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new Pseudograph<V, E>(null, edgeSupplier, false));
    }
}

