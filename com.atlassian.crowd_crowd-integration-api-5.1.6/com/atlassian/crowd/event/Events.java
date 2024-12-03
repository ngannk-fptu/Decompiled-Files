/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.model.event.OperationEvent;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;

public class Events {
    private final Iterable<OperationEvent> events;
    private final String newEventToken;

    public Events(Iterable<OperationEvent> events, String newEventToken) {
        this.events = Iterables.unmodifiableIterable(events);
        this.newEventToken = newEventToken;
    }

    public Iterable<OperationEvent> getEvents() {
        return this.events;
    }

    public String getNewEventToken() {
        return this.newEventToken;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("events", this.events).add("newToken", (Object)this.newEventToken).toString();
    }
}

