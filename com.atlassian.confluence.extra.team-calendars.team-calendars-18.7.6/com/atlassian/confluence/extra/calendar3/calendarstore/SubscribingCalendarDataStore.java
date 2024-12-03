/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;

public interface SubscribingCalendarDataStore<T extends SubscribingSubCalendar>
extends CalendarDataStore<T> {
    public PersistedSubCalendar getSourceSubCalendar(String var1);
}

