/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.events.AbstractSubCalendarLoadEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public class SubCalendarLoaded
extends AbstractSubCalendarLoadEvent {
    public SubCalendarLoaded(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar persistedSubCalendar, long timeTaken) {
        super(eventSource, trigger, persistedSubCalendar, timeTaken);
    }

    @Override
    protected String calculateEventNameInternal() {
        return "teamcalendars.subcalendar.load.metadata";
    }
}

