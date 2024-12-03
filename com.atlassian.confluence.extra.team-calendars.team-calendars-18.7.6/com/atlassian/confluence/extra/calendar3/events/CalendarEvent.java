/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class CalendarEvent
extends ConfluenceEvent {
    private final ConfluenceUser trigger;

    public CalendarEvent(Object eventSource, ConfluenceUser trigger) {
        super(eventSource);
        this.trigger = trigger;
    }

    public ConfluenceUser getTrigger() {
        return this.trigger;
    }
}

