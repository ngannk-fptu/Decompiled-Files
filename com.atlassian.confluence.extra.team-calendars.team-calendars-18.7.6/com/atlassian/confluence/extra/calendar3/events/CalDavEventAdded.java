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
@EventName(value="teamcalendars.add.event.calDav")
public class CalDavEventAdded
extends CalendarEvent {
    private final boolean isNew;
    private final boolean isReschedule;

    public CalDavEventAdded(Object eventSource, ConfluenceUser trigger, boolean isNew, boolean isReschedule) {
        super(eventSource, trigger);
        this.isNew = isNew;
        this.isReschedule = isReschedule;
    }

    public boolean isNew() {
        return this.isNew;
    }

    public boolean isReschedule() {
        return this.isReschedule;
    }
}

