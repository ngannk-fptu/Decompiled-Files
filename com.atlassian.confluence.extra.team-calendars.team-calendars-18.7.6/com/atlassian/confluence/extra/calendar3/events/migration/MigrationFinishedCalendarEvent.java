/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events.migration;

import com.atlassian.confluence.extra.calendar3.events.migration.ProgressCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class MigrationFinishedCalendarEvent
extends ProgressCalendarEvent {
    public MigrationFinishedCalendarEvent(Object eventSource, ConfluenceUser trigger) {
        super(eventSource, trigger);
    }

    @Override
    public float getProgress() {
        return 1.0f;
    }
}

