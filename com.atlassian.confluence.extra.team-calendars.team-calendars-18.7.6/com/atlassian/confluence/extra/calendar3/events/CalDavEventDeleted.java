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
@EventName(value="teamcalendars.delete.event.calDav")
public class CalDavEventDeleted
extends CalendarEvent {
    private final boolean isReschedule;

    public CalDavEventDeleted(Object eventSource, ConfluenceUser trigger, boolean isReschedule) {
        super(eventSource, trigger);
        this.isReschedule = isReschedule;
    }

    public boolean isReschedule() {
        return this.isReschedule;
    }
}

