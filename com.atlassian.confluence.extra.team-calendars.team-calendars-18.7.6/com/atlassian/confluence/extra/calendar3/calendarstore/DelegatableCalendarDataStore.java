/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;

public interface DelegatableCalendarDataStore<T extends PersistedSubCalendar>
extends CalendarDataStore<T> {
    public boolean handles(SubCalendar var1);
}

