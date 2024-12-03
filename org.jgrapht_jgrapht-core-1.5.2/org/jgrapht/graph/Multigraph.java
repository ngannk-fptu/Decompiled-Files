/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class Multigraph<V, E>
extends AbstractBaseGraph<V, E> {
    private static final long serialVersionUID = -8313058939737164595L;

    public Multigraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false);
    }

    public Multigraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().undirected().allowMultipleEdges(true).allowSelfLoops(false).weighted(weighted).build());
    }

    public static <V, E> GraphBuilder<V, E, ? extends Multigraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new Multigraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends Multigraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new Multigraph<V, E>(null, edgeSupplier, false));
    }
}

