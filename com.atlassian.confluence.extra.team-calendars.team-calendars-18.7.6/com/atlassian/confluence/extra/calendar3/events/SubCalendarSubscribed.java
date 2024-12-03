/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.PrivateCalendarUrlManager;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class SubCalendarSubscribed
extends CalendarEvent {
    public SubCalendarSubscribed(PrivateCalendarUrlManager eventSource, ConfluenceUser trigger) {
        super(eventSource, trigger);
    }
}

