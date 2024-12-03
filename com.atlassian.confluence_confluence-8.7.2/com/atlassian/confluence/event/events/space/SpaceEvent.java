/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.spaces.Space;

public abstract class SpaceEvent
extends ConfluenceEvent {
    protected Space space;

    public SpaceEvent(Object src) {
        super(src);
    }

    public SpaceEvent(Object src, Space space) {
        super(src);
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpaceEvent event = (SpaceEvent)o;
        return !(this.space != null ? !this.space.equals(event.space) : event.space != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.space != null ? this.space.hashCode() : 0);
        return result;
    }
}

