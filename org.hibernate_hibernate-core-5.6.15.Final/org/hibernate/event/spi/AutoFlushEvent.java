/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.util.Set;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEvent;

public class AutoFlushEvent
extends FlushEvent {
    private Set querySpaces;
    private boolean flushRequired;

    public AutoFlushEvent(Set querySpaces, EventSource source) {
        super(source);
        this.querySpaces = querySpaces;
    }

    public Set getQuerySpaces() {
        return this.querySpaces;
    }

    public void setQuerySpaces(Set querySpaces) {
        this.querySpaces = querySpaces;
    }

    public boolean isFlushRequired() {
        return this.flushRequired;
    }

    public void setFlushRequired(boolean dirty) {
        this.flushRequired = dirty;
    }
}

