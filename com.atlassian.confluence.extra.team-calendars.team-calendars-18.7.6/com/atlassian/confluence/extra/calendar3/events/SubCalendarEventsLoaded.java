/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.events.AbstractSubCalendarLoadEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SubCalendarEventsLoaded
extends AbstractSubCalendarLoadEvent {
    private static final DateTimeFormatter RANGE_FORMATTER = DateTimeFormat.forPattern((String)"yyyy-MM-dd HH:mm:ss.SSS Z").withZone(DateTimeZone.UTC);
    private final DateTime rangeStart;
    private final DateTime rangeEnd;
    private Set<SubCalendarEvent> eventsLoaded;

    public SubCalendarEventsLoaded(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar, long timeTaken, DateTime rangeStart, DateTime rangeEnd, Set<SubCalendarEvent> eventsLoaded) {
        super(eventSource, trigger, subCalendar, timeTaken);
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.eventsLoaded = eventsLoaded;
    }

    @Override
    protected String calculateEventNameInternal() {
        return "teamcalendars.event.load.type." + StringUtils.defaultString(this.getSubscriptionType(), this.getSubCalendarType());
    }

    public int getEventCount() {
        return this.eventsLoaded == null ? 0 : this.eventsLoaded.size();
    }

    public String getRangeStart() {
        return this.getFormattedDate(this.rangeStart);
    }

    private String getFormattedDate(DateTime dateTime) {
        return RANGE_FORMATTER.print((ReadableInstant)dateTime);
    }

    public String getRangeEnd() {
        return this.getFormattedDate(this.rangeEnd);
    }
}

