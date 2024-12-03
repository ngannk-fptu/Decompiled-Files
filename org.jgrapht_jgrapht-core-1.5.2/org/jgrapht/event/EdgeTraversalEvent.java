/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import java.util.EventObject;

public class EdgeTraversalEvent<E>
extends EventObject {
    private static final long serialVersionUID = 4050768173789820979L;
    protected E edge;

    public EdgeTraversalEvent(Object eventSource, E edge) {
        super(eventSource);
        this.edge = edge;
    }

    public E getEdge() {
        return this.edge;
    }
}

