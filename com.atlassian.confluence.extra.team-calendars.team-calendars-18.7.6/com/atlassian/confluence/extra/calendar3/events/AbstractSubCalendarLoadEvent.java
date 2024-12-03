/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public abstract class AbstractSubCalendarLoadEvent
extends BaseSubCalendarEvent {
    private final long timeTaken;

    protected AbstractSubCalendarLoadEvent(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar, long timeTaken) {
        super(eventSource, trigger, subCalendar);
        this.timeTaken = timeTaken;
    }

    public long getTimeTaken() {
        return this.timeTaken;
    }

    @EventName
    public String calculateEventName() {
        return this.calculateEventNameInternal();
    }

    protected abstract String calculateEventNameInternal();

    public String getSubCalendarId() {
        return ((PersistedSubCalendar)this.getSubCalendar()).getId();
    }

    public String getSubCalendarParentId() {
        return ((PersistedSubCalendar)this.getSubCalendar()).getParentId();
    }

    public String getSubCalendarType() {
        return ((PersistedSubCalendar)this.getSubCalendar()).getType();
    }

    public boolean isSubscription() {
        return this.getSubCalendar() instanceof SubscribingSubCalendar;
    }

    public String getSubscriptionId() {
        return this.isSubscription() ? ((SubscribingSubCalendar)this.getSubCalendar()).getSubscriptionId() : null;
    }

    public String getSubscriptionType() {
        return this.isSubscription() ? ((SubscribingSubCalendar)this.getSubCalendar()).getSubscriptionType() : null;
    }
}

