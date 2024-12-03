/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events.migration;

import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.events.migration.ProgressCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class BandanaProviderProgressCalendarEvent
extends ProgressCalendarEvent {
    public BandanaProviderProgressCalendarEvent(BandanaSubCalendarsProvider eventSource, ConfluenceUser trigger) {
        this(eventSource, trigger, 0.0f);
    }

    public BandanaProviderProgressCalendarEvent(BandanaSubCalendarsProvider eventSource, ConfluenceUser trigger, float progress) {
        super(eventSource, trigger);
        this.setProgress(progress);
    }
}

