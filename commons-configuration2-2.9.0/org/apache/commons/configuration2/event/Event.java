/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.event;

import java.util.EventObject;
import org.apache.commons.configuration2.event.EventType;

public class Event
extends EventObject {
    public static final EventType<Event> ANY = new EventType(null, "ANY");
    private static final long serialVersionUID = -8168310049858198944L;
    private static final String FMT_PROPERTY = " %s=%s";
    private static final int BUF_SIZE = 256;
    private final EventType<? extends Event> eventType;

    public Event(Object source, EventType<? extends Event> evType) {
        super(source);
        if (evType == null) {
            throw new IllegalArgumentException("Event type must not be null!");
        }
        this.eventType = evType;
    }

    public EventType<? extends Event> getEventType() {
        return this.eventType;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);
        buf.append(this.getClass().getSimpleName());
        buf.append(" [");
        this.appendPropertyRepresentation(buf, "source", this.getSource());
        this.appendPropertyRepresentation(buf, "eventType", this.getEventType());
        buf.append(" ]");
        return buf.toString();
    }

    protected void appendPropertyRepresentation(StringBuilder buf, String property, Object value) {
        buf.append(String.format(FMT_PROPERTY, property, String.valueOf(value)));
    }
}

