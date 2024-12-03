/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.GraphDelegator;

public class AsUnweightedGraph<V, E>
extends GraphDelegator<V, E>
implements Serializable,
Graph<V, E> {
    private static final long serialVersionUID = -5186421272597767751L;
    private static final String EDGE_WEIGHT_IS_NOT_SUPPORTED = "Edge weight is not supported";

    public AsUnweightedGraph(Graph<V, E> g) {
        super(Objects.requireNonNull(g));
    }

    @Override
    public double getEdgeWeight(E e) {
        return 1.0;
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        throw new UnsupportedOperationException(EDGE_WEIGHT_IS_NOT_SUPPORTED);
    }

    @Override
    public GraphType getType() {
        return super.getType().asUnweighted();
    }
}

