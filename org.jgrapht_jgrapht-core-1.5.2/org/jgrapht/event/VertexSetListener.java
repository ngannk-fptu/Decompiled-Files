/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import java.util.EventListener;
import org.jgrapht.event.GraphVertexChangeEvent;

public interface VertexSetListener<V>
extends EventListener {
    public void vertexAdded(GraphVertexChangeEvent<V> var1);

    public void vertexRemoved(GraphVertexChangeEvent<V> var1);
}

