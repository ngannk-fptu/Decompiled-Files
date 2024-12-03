/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.util.Comparator;
import org.jgrapht.Graph;

public class VertexDegreeComparator<V, E>
implements Comparator<V> {
    private Graph<V, E> graph;
    private Order order;

    public static <V> Comparator<V> of(Graph<V, ?> g) {
        return Comparator.comparingInt(g::degreeOf);
    }

    @Deprecated(forRemoval=true, since="1.5.1")
    public VertexDegreeComparator(Graph<V, E> g) {
        this(g, Order.ASCENDING);
    }

    @Deprecated(forRemoval=true, since="1.5.1")
    public VertexDegreeComparator(Graph<V, E> g, Order order) {
        this.graph = g;
        this.order = order;
    }

    @Override
    @Deprecated(forRemoval=true, since="1.5.1")
    public int compare(V v1, V v2) {
        int comparison = Integer.compare(this.graph.degreeOf(v1), this.graph.degreeOf(v2));
        if (this.order == Order.ASCENDING) {
            return comparison;
        }
        return -1 * comparison;
    }

    @Deprecated(forRemoval=true, since="1.5.1")
    public static enum Order {
        ASCENDING,
        DESCENDING;

    }
}

