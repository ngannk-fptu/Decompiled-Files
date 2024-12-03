/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class CalendarDashboardViewEvent
extends CalendarEvent {
    private final boolean isOnSpace;

    public CalendarDashboardViewEvent(boolean isOnSpace, Object eventSource, ConfluenceUser trigger) {
        super(eventSource, trigger);
        this.isOnSpace = isOnSpace;
    }

    public String getContext() {
        return this.isOnSpace ? "spaceCalendars" : "myCalendars";
    }

    @EventName
    public String calculateEventName() {
        return "teamcalendars.view.render-server";
    }
}

