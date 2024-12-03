/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class PluginLicensedEvent
extends CalendarEvent {
    public PluginLicensedEvent(Object eventSource, ConfluenceUser trigger) {
        super(eventSource, trigger);
    }
}

