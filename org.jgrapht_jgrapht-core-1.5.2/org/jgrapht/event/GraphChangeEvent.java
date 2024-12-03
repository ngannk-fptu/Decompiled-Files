/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.event;

import java.util.EventObject;

public class GraphChangeEvent
extends EventObject {
    private static final long serialVersionUID = 3834592106026382391L;
    protected int type;

    public GraphChangeEvent(Object eventSource, int type) {
        super(eventSource);
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}

