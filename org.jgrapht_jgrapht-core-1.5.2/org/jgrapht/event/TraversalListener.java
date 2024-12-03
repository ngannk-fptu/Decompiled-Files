/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.VertexTraversalEvent;

public interface TraversalListener<V, E> {
    public void connectedComponentFinished(ConnectedComponentTraversalEvent var1);

    public void connectedComponentStarted(ConnectedComponentTraversalEvent var1);

    public void edgeTraversed(EdgeTraversalEvent<E> var1);

    public void vertexTraversed(VertexTraversalEvent<V> var1);

    public void vertexFinished(VertexTraversalEvent<V> var1);
}

