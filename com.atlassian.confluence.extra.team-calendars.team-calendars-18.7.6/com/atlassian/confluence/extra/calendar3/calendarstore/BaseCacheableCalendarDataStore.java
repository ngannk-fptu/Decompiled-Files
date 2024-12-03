/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;

public abstract class BaseCacheableCalendarDataStore<T extends PersistedSubCalendar>
extends AbstractCalendarDataStore<T> {
    protected BaseCacheableCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor) {
        super(dataStoreCommonPropertyAccessor);
    }

    protected String getSubCalendarDataCacheKey(T subCalendar) {
        return ((PersistedSubCalendar)subCalendar).getId();
    }
}

