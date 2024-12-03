/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.SingleSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.AsynchronousPreferred;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;

@AsynchronousPreferred
public class SubCalendarEventMoved
extends SingleSubCalendarEvent {
    private SubCalendarEvent previousSubCalendarEvent;
    private Collection<SubCalendarEvent> previousSubCalendarEventRescheduledRecurrences;
    private Collection<SubCalendarEvent> subCalendarEventRescheduledRecurrences;

    public SubCalendarEventMoved(CalendarManager eventSource, ConfluenceUser trigger, SubCalendarEvent previousSubCalendarEvent, Collection<SubCalendarEvent> previousSubCalendarEventRescheduledRecurrences, SubCalendarEvent subCalendarEvent, Collection<SubCalendarEvent> subCalendarEventRescheduledRecurrences) {
        super(eventSource, trigger, subCalendarEvent);
        this.previousSubCalendarEvent = previousSubCalendarEvent;
        this.previousSubCalendarEventRescheduledRecurrences = null == previousSubCalendarEventRescheduledRecurrences ? Collections.emptySet() : Collections.unmodifiableCollection(previousSubCalendarEventRescheduledRecurrences);
        this.subCalendarEventRescheduledRecurrences = null == subCalendarEventRescheduledRecurrences ? Collections.emptySet() : Collections.unmodifiableCollection(subCalendarEventRescheduledRecurrences);
    }

    public SubCalendarEvent getPreviousSubCalendarEvent() {
        return this.previousSubCalendarEvent;
    }

    public Collection<SubCalendarEvent> getPreviousSubCalendarEventRescheduledRecurrences() {
        return this.previousSubCalendarEventRescheduledRecurrences;
    }

    public Collection<SubCalendarEvent> getSubCalendarEventRescheduledRecurrences() {
        return this.subCalendarEventRescheduledRecurrences;
    }

    public SubCalendarEvent getUpdateFor(SubCalendarEvent previousSubCalendarEvent) {
        String recurrenceId = previousSubCalendarEvent.getRecurrenceId();
        if (StringUtils.isBlank(recurrenceId)) {
            return StringUtils.equals(this.getEvent().getUid(), previousSubCalendarEvent.getUid()) ? this.getEvent() : null;
        }
        Collection matchingRescheduleRecurrences = Collections2.filter(this.getSubCalendarEventRescheduledRecurrences(), subCalendarEvent -> StringUtils.equals(recurrenceId, subCalendarEvent.getRecurrenceId()));
        return matchingRescheduleRecurrences.isEmpty() ? null : (SubCalendarEvent)matchingRescheduleRecurrences.iterator().next();
    }
}

