/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.event.events.ConfluenceEvent;

public abstract class LookAndFeelEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -5255951683750985250L;
    private final String spaceKey;

    public LookAndFeelEvent(Object src, String spaceKey) {
        super(src);
        this.spaceKey = spaceKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public boolean isGlobal() {
        return this.spaceKey == null;
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
        LookAndFeelEvent event = (LookAndFeelEvent)o;
        return !(this.spaceKey != null ? !this.spaceKey.equals(event.spaceKey) : event.spaceKey != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.spaceKey != null ? this.spaceKey.hashCode() : 0);
        return result;
    }
}

