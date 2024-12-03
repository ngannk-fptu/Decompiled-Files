/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events.migration;

import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public abstract class ProgressCalendarEvent
extends CalendarEvent {
    private float progress = 0.0f;

    public ProgressCalendarEvent(Object eventSource, ConfluenceUser trigger) {
        super(eventSource, trigger);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        if (progress <= 0.0f) {
            this.progress = 0.0f;
            return;
        }
        if (progress >= 1.0f) {
            this.progress = 1.0f;
            return;
        }
        this.progress = progress;
    }

    public String toString() {
        return this.getSource() != null ? String.format("Processing on %s  with progress [%d%%]", this.getSource().toString(), Math.round(this.getProgress() * 100.0f)) : "Null event source !!!";
    }
}

