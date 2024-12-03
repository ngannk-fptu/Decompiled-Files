/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.VertexSetListener;

public interface ListenableGraph<V, E>
extends Graph<V, E> {
    public void addGraphListener(GraphListener<V, E> var1);

    public void addVertexSetListener(VertexSetListener<V> var1);

    public void removeGraphListener(GraphListener<V, E> var1);

    public void removeVertexSetListener(VertexSetListener<V> var1);
}

