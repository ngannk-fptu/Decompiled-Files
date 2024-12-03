/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.function.Supplier;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;

public class WeightedMultigraph<V, E>
extends Multigraph<V, E> {
    private static final long serialVersionUID = -6009321659287373874L;

    public WeightedMultigraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass));
    }

    public WeightedMultigraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, true);
    }

    public static <V, E> GraphBuilder<V, E, ? extends WeightedMultigraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new WeightedMultigraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends WeightedMultigraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new WeightedMultigraph<V, E>(null, edgeSupplier));
    }
}

