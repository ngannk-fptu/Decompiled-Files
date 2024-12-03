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
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.events.ReminderSetingEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="teamcalendars.reminder.setting.created")
public class ReminderSettingCreated
extends ReminderSetingEvent {
    public ReminderSettingCreated(ReminderSettingCallback.ReminderSettingChange eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar) {
        super(eventSource, trigger, subCalendar);
    }
}

