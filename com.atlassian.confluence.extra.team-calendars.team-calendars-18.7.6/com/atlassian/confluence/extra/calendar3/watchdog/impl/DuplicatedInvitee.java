/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

public class DuplicatedInvitee {
    private final int id;
    private final int eventId;
    private final String inviteeId;

    public int getId() {
        return this.id;
    }

    public int getEventId() {
        return this.eventId;
    }

    public String getInviteeId() {
        return this.inviteeId;
    }

    public DuplicatedInvitee(int id, int eventId, String inviteeId) {
        this.id = id;
        this.eventId = eventId;
        this.inviteeId = inviteeId;
    }
}

