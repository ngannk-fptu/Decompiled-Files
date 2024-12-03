/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import org.apache.jackrabbit.spi.Event;
import org.apache.jackrabbit.spi.EventBundle;

public class EventBundleImpl
implements EventBundle,
Serializable {
    private final boolean isLocal;
    private final Collection<Event> events;

    public EventBundleImpl(Collection<Event> events, boolean isLocal) {
        this.events = events;
        this.isLocal = isLocal;
    }

    @Override
    public Iterator<Event> getEvents() {
        return this.events.iterator();
    }

    @Override
    public boolean isLocal() {
        return this.isLocal;
    }

    @Override
    public Iterator<Event> iterator() {
        return this.getEvents();
    }
}

