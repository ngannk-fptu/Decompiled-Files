/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;

public class LightweightPersistentSubCalendar
extends PersistedSubCalendar {
    private final String subCalendarId;

    public LightweightPersistentSubCalendar(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    @Override
    public String getId() {
        return this.subCalendarId;
    }

    @Override
    public String getCreator() {
        throw new UnsupportedOperationException("LightweightPersistentSubCalendar unsupport this method");
    }

    @Override
    public String getSpaceName() {
        throw new UnsupportedOperationException("LightweightPersistentSubCalendar unsupport this method");
    }

    @Override
    public boolean isWatchable() {
        throw new UnsupportedOperationException("LightweightPersistentSubCalendar unsupport this method");
    }

    @Override
    public boolean isRestrictable() {
        throw new UnsupportedOperationException("LightweightPersistentSubCalendar unsupport this method");
    }

    @Override
    public boolean isEventInviteesSupported() {
        throw new UnsupportedOperationException("LightweightPersistentSubCalendar unsupport this method");
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException("LightweightPersistentSubCalendar unsupport this method");
    }
}

