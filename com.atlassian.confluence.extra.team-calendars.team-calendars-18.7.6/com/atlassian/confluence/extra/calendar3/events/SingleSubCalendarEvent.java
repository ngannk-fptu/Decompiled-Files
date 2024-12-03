/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class SingleSubCalendarEvent
extends CalendarEvent {
    private SubCalendarEvent event;

    public SingleSubCalendarEvent(CalendarManager eventSource, ConfluenceUser trigger, SubCalendarEvent event) {
        super(eventSource, trigger);
        this.event = event;
    }

    public SubCalendarEvent getEvent() {
        return this.event;
    }

    public PersistedSubCalendar getSubCalendar() {
        SubCalendarEvent event = this.getEvent();
        return null == event ? null : event.getSubCalendar();
    }
}

