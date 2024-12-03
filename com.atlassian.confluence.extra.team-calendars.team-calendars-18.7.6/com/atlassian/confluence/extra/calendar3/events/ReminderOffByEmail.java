/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

@EventName(value="teamcalendars.reminder.email.off")
public class ReminderOffByEmail
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    private String userKey;

    public ReminderOffByEmail(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar) {
        super(eventSource, trigger, subCalendar);
        this.setUserKey(trigger.getKey().getStringValue());
    }

    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}

