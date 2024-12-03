/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DirectedWeightedMultigraph<V, E>
extends DirectedMultigraph<V, E> {
    private static final long serialVersionUID = 1984381120642160572L;

    public DirectedWeightedMultigraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public DirectedWeightedMultigraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedWeightedMultigraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DirectedWeightedMultigraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedWeightedMultigraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DirectedWeightedMultigraph<V, E>(null, edgeSupplier));
    }
}

