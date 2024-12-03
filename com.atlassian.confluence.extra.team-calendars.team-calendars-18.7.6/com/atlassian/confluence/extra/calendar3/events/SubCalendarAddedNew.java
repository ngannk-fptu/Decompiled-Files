/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class SubCalendarAddedNew
extends CalendarEvent {
    private final boolean isOnSpace;

    public SubCalendarAddedNew(Object eventSource, ConfluenceUser trigger, boolean isOnSpace) {
        super(eventSource, trigger);
        this.isOnSpace = isOnSpace;
    }

    @EventName
    public String calculateEventName() {
        return this.isOnSpace ? "teamcalendars.subcalendar.created.by.add.new.spaceCalendars" : "teamcalendars.subcalendar.created.by.add.new.myCalendars";
    }
}

