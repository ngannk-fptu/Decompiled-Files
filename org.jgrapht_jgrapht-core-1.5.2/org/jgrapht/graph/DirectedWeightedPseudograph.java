/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class DirectedWeightedPseudograph<V, E>
extends DirectedPseudograph<V, E> {
    private static final long serialVersionUID = -4775269773843490859L;

    public DirectedWeightedPseudograph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public DirectedWeightedPseudograph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedWeightedPseudograph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DirectedWeightedPseudograph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedWeightedPseudograph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DirectedWeightedPseudograph<V, E>(null, edgeSupplier));
    }
}

