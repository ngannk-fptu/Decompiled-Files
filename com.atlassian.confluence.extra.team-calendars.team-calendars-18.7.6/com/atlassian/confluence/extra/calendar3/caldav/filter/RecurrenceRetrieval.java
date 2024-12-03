/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrievalMode;
import java.util.Optional;
import org.bedework.caldav.util.TimeRange;

public class RecurrenceRetrieval {
    private RecurrenceRetrievalMode recurrenceRetrievalMode;
    private Optional<TimeRange> timeRange;

    public RecurrenceRetrievalMode getRecurrenceRetrievalMode() {
        return this.recurrenceRetrievalMode;
    }

    public Optional<TimeRange> getTimeRange() {
        return this.timeRange;
    }

    public RecurrenceRetrieval(RecurrenceRetrievalMode recurrenceRetrievalMode) {
        this(recurrenceRetrievalMode, null);
    }

    public RecurrenceRetrieval(RecurrenceRetrievalMode recurrenceRetrievalMode, TimeRange timeRange) {
        this.recurrenceRetrievalMode = recurrenceRetrievalMode;
        this.timeRange = Optional.ofNullable(timeRange);
    }
}

