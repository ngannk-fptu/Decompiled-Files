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

public class SubCalendarIndexOutOfSynch
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    public SubCalendarIndexOutOfSynch(Object eventSource, ConfluenceUser trigger) {
        super(eventSource, trigger, null);
    }
}

