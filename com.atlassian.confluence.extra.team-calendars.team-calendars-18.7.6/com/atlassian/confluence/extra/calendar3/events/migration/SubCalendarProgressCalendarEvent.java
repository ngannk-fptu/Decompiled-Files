/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events.migration;

import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.events.migration.BandanaProviderProgressCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class SubCalendarProgressCalendarEvent
extends BandanaProviderProgressCalendarEvent {
    private final String subCalendarId;

    public SubCalendarProgressCalendarEvent(BandanaSubCalendarsProvider eventSource, ConfluenceUser trigger, String subCalendarId) {
        this(eventSource, trigger, subCalendarId, 0.0f);
    }

    public SubCalendarProgressCalendarEvent(BandanaSubCalendarsProvider eventSource, ConfluenceUser trigger, String subCalendarId, float progress) {
        super(eventSource, trigger, progress);
        this.subCalendarId = subCalendarId;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    @Override
    public String toString() {
        return this.getSource() != null ? String.format("Processing on %s - sub calendar id (%s) with progress [%d%%] ", this.getSource().toString(), this.getSubCalendarId(), Math.round(this.getProgress() * 100.0f)) : "Null event source !!!";
    }
}

