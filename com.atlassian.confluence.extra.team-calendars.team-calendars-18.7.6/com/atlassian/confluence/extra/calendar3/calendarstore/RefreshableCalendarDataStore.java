/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public interface RefreshableCalendarDataStore<T extends PersistedSubCalendar>
extends CalendarDataStore<T> {
    public boolean hasReloadEventsPrivilege(T var1, ConfluenceUser var2);

    public void refresh(T var1);
}

