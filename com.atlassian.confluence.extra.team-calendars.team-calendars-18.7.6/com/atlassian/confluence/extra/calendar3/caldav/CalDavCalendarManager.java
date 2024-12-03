/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import java.util.Collection;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

public interface CalDavCalendarManager {
    public Calendar toCalendar(PersistedSubCalendar var1, Collection<SubCalendarEvent> var2) throws Exception;

    public Collection<SubCalendarEvent> query(PersistedSubCalendar var1, FilterBase var2, RecurrenceRetrieval var3) throws Exception;

    public Collection<SubCalendarEvent> getEvents(PersistedSubCalendar var1, Predicate<VEvent> var2, String ... var3) throws Exception;

    public Calendar transform(PersistedSubCalendar var1, Calendar var2) throws Exception;
}

