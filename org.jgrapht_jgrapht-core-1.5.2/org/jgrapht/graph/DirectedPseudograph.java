/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DirectedPseudograph<V, E>
extends AbstractBaseGraph<V, E> {
    private static final long serialVersionUID = -7461248851245878913L;

    public DirectedPseudograph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false);
    }

    public DirectedPseudograph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().directed().allowMultipleEdges(true).allowSelfLoops(true).weighted(weighted).build());
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedPseudograph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DirectedPseudograph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedPseudograph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DirectedPseudograph<V, E>(null, edgeSupplier, false));
    }
}

