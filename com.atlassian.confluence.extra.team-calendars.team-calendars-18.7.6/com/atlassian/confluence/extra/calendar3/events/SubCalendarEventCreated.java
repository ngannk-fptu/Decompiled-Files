/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.SingleSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class SubCalendarEventCreated
extends SingleSubCalendarEvent {
    public SubCalendarEventCreated(CalendarManager eventSource, ConfluenceUser trigger, SubCalendarEvent subCalendarEvent) {
        super(eventSource, trigger, subCalendarEvent);
    }
}

