/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.SingleSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Collection;
import java.util.Collections;

@AsynchronousPreferred
public class SubCalendarEventRemoved
extends SingleSubCalendarEvent {
    private Collection<SubCalendarEvent> rescheduledRecurrences;

    public SubCalendarEventRemoved(CalendarManager eventSource, ConfluenceUser trigger, SubCalendarEvent baseEvent, Collection<SubCalendarEvent> rescheduledRecurrences) {
        super(eventSource, trigger, baseEvent);
        this.rescheduledRecurrences = null != rescheduledRecurrences ? Collections.unmodifiableCollection(rescheduledRecurrences) : Collections.emptySet();
    }

    public Collection<SubCalendarEvent> getRescheduledRecurrences() {
        return this.rescheduledRecurrences;
    }

    @Override
    public PersistedSubCalendar getSubCalendar() {
        if (this.getEvent() == null) {
            return this.rescheduledRecurrences.stream().findFirst().get().getSubCalendar();
        }
        return super.getSubCalendar();
    }
}

