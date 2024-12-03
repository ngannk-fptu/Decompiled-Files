/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;

public class TraversalListenerAdapter<V, E>
implements TraversalListener<V, E> {
    @Override
    public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
    }

    @Override
    public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
    }

    @Override
    public void edgeTraversed(EdgeTraversalEvent<E> e) {
    }

    @Override
    public void vertexTraversed(VertexTraversalEvent<V> e) {
    }

    @Override
    public void vertexFinished(VertexTraversalEvent<V> e) {
    }
}

