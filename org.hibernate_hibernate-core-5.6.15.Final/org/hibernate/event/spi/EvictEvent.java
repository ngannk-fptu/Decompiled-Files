/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;

public class EvictEvent
extends AbstractEvent {
    private Object object;

    public EvictEvent(Object object, EventSource source) {
        super(source);
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

