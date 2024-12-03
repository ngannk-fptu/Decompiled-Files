/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event;

@Deprecated
public abstract class Event {
    private final Object source;
    private long timestamp;

    public Event(Object source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    @Deprecated
    public Object getSource() {
        return this.source;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

