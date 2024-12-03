/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.Set;
import net.fortuna.ical4j.model.Calendar;

public class SubCalendarRemoved
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    private final Calendar subCalendarData;
    private final Set<String> subscribers;
    private final Collection<PersistedSubCalendar> subscriptions;

    public SubCalendarRemoved(CalendarManager eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar, Calendar subCalendarData, Set<String> subscribers, Collection<PersistedSubCalendar> subscriptions) {
        super(eventSource, trigger, subCalendar);
        this.subCalendarData = subCalendarData;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
    }

    @EventName
    public String calculateEventName() {
        return "teamcalendars.subcalendar.remove." + ((PersistedSubCalendar)this.getSubCalendar()).getType();
    }

    public Calendar getSubCalendarData() {
        return this.subCalendarData;
    }

    public Set<String> getSubscribers() {
        return this.subscribers;
    }

    public Collection<PersistedSubCalendar> getSubscriptions() {
        return this.subscriptions;
    }
}

