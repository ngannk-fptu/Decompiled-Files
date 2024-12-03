/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions;

import java.time.Instant;

public class StateChangeInformation {
    private final long id;
    private final String message;
    private final MessageLevel level;
    private final Instant eventTimestamp;

    public StateChangeInformation(long id, String message, MessageLevel level, long timestamp) {
        this.id = id;
        this.message = message;
        this.level = level;
        this.eventTimestamp = Instant.ofEpochMilli(timestamp);
    }

    public long getId() {
        return this.id;
    }

    public String getMessage() {
        return this.message;
    }

    public Instant getEventTimestamp() {
        return this.eventTimestamp;
    }

    public MessageLevel getLevel() {
        return this.level;
    }

    public static enum MessageLevel {
        INFO,
        WARNING,
        ERROR;

    }
}

