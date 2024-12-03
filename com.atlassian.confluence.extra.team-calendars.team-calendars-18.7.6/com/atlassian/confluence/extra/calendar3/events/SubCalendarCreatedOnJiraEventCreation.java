/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public class SubCalendarCreatedOnJiraEventCreation
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    public SubCalendarCreatedOnJiraEventCreation(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar) {
        super(eventSource, trigger, subCalendar);
    }
}

