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

public class SubCalendarWatched
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    public SubCalendarWatched(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar) {
        super(eventSource, trigger, subCalendar);
    }

    @EventName
    public String calculateEventName() {
        return "teamcalendars.subcalendar.watch." + ((PersistedSubCalendar)this.getSubCalendar()).getType();
    }
}

