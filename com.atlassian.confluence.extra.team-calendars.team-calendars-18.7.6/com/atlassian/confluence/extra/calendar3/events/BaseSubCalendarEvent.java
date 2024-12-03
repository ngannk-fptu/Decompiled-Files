/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public class BaseSubCalendarEvent<T extends PersistedSubCalendar>
extends CalendarEvent {
    private T subCalendar;

    public BaseSubCalendarEvent(Object eventSource, ConfluenceUser trigger, T subCalendar) {
        super(eventSource, trigger);
        this.subCalendar = subCalendar;
    }

    public T getSubCalendar() {
        return this.subCalendar;
    }
}

