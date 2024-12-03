/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.atlassian.event;

import org.springframework.context.ApplicationEvent;

@Deprecated
public class Event
extends ApplicationEvent {
    public Event(Object source) {
        super(source);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event event = (Event)((Object)o);
        return !(this.source != null ? !this.source.equals(event.source) : event.source != null);
    }

    public int hashCode() {
        return this.source != null ? this.source.hashCode() : 0;
    }
}

