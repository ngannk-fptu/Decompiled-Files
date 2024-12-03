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
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Collection;

@AsynchronousPreferred
@EventName(value="teamcalendars.reminder.sent")
public class ReminderNotificationEvent
extends CalendarEvent {
    public ReminderNotificationEvent(ConfluenceUser trigger, Collection<ReminderEvent> notifyEvent) {
        super(notifyEvent, trigger);
    }
}

