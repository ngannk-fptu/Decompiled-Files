/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

public class DuplicatedEventRecurrenceExclusion {
    private int id;
    private int eventId;
    private long exclusion;
    private boolean allDay;

    public DuplicatedEventRecurrenceExclusion(int id, int eventId, long exclusion, boolean allDay) {
        this.id = id;
        this.eventId = eventId;
        this.exclusion = exclusion;
        this.allDay = allDay;
    }

    public int getId() {
        return this.id;
    }

    public int getEventId() {
        return this.eventId;
    }

    public long getExclusion() {
        return this.exclusion;
    }

    public boolean isAllDay() {
        return this.allDay;
    }
}

