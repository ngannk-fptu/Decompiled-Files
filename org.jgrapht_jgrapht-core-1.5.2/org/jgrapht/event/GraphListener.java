/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.VertexSetListener;

public interface GraphListener<V, E>
extends VertexSetListener<V> {
    public void edgeAdded(GraphEdgeChangeEvent<V, E> var1);

    public void edgeRemoved(GraphEdgeChangeEvent<V, E> var1);

    default public void edgeWeightUpdated(GraphEdgeChangeEvent<V, E> e) {
    }
}

