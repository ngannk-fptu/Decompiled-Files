/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.event.EventRepresentation;

public interface NotificationEventProvider {
    public Iterable<EventRepresentation> getAllEvents();

    public EventRepresentation getEvent(String var1);

    public String getEventKey(Object var1);
}

