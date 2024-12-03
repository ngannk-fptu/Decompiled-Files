/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.api;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AuditEntityCursor {
    private final Instant timestamp;
    private final long id;

    public AuditEntityCursor(@Nonnull Instant timestamp, long id) {
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.id = id;
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp.truncatedTo(ChronoUnit.MILLIS);
    }

    public long getId() {
        return this.id;
    }
}

