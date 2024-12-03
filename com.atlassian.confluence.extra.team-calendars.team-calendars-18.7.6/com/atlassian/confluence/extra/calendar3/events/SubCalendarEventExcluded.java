/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.SingleSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;
import org.joda.time.DateTime;

@AsynchronousPreferred
public class SubCalendarEventExcluded
extends SingleSubCalendarEvent {
    private final DateTime excludeDate;

    public SubCalendarEventExcluded(CalendarManager eventSource, ConfluenceUser trigger, SubCalendarEvent baseEvent, DateTime excludeDate) {
        super(eventSource, trigger, baseEvent);
        this.excludeDate = excludeDate;
    }

    public DateTime getExcludeDate() {
        return this.excludeDate;
    }
}

