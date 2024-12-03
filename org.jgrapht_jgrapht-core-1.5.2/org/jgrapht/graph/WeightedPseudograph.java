/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class WeightedPseudograph<V, E>
extends Pseudograph<V, E> {
    private static final long serialVersionUID = 3037964528481084240L;

    public WeightedPseudograph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public WeightedPseudograph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends WeightedPseudograph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new WeightedPseudograph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends WeightedPseudograph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new WeightedPseudograph<V, E>(null, edgeSupplier));
    }
}

