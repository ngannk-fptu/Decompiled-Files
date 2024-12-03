/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class ParentSubCalendarRefreshed
extends BaseSubCalendarEvent<ParentSubCalendarDataStore.ParentSubCalendar> {
    public ParentSubCalendarRefreshed(Object eventSource, ConfluenceUser trigger, ParentSubCalendarDataStore.ParentSubCalendar subCalendar) {
        super(eventSource, trigger, subCalendar);
    }
}

