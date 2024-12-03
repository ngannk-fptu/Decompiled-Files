/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEvent;

public class DirtyCheckEvent
extends FlushEvent {
    private boolean dirty;

    public DirtyCheckEvent(EventSource source) {
        super(source);
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

