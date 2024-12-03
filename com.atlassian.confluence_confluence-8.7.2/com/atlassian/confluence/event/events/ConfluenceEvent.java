/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events;

import com.atlassian.event.Event;
import java.io.Serializable;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ConfluenceEvent
extends Event
implements Serializable {
    public ConfluenceEvent(Object src) {
        super(src);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return this.getClass().equals(obj.getClass());
    }

    public int hashCode() {
        return super.hashCode();
    }
}

