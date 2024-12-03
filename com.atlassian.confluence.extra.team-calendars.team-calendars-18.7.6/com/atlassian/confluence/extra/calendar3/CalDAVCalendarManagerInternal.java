/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import net.fortuna.ical4j.model.Calendar;
import org.joda.time.DateTime;

public interface CalDAVCalendarManagerInternal {
    public Collection<SubCalendarEvent> query(ConfluenceUser var1, PersistedSubCalendar var2, FilterBase var3, RecurrenceRetrieval var4) throws Exception;

    public void removeEventOnHierarchy(PersistedSubCalendar var1, String var2, String var3) throws Exception;

    public void excludeEventOnHierarchy(PersistedSubCalendar var1, String var2, DateTime var3) throws Exception;

    public Calendar transform(PersistedSubCalendar var1, Calendar var2) throws Exception;
}

