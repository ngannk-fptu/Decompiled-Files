/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import com.google.common.base.MoreObjects;
import javax.annotation.Nullable;

public class EventStreamTombstone
extends AbstractTombstone {
    private String reason;
    @Nullable
    private Long directoryId;

    protected EventStreamTombstone() {
    }

    public static EventStreamTombstone createGlobal(long timestamp, String reason) {
        return new EventStreamTombstone(timestamp, reason, null);
    }

    public static EventStreamTombstone createForDirectory(long timestamp, long directoryId, String reason) {
        return new EventStreamTombstone(timestamp, reason, directoryId);
    }

    public EventStreamTombstone(long timestamp, String reason, @Nullable Long directoryId) {
        super(timestamp);
        this.reason = reason;
        this.directoryId = directoryId;
    }

    public String getReason() {
        return this.reason;
    }

    protected void setReason(String reason) {
        this.reason = reason;
    }

    @Nullable
    public Long getDirectoryId() {
        return this.directoryId;
    }

    protected void setDirectoryId(@Nullable Long directoryId) {
        this.directoryId = directoryId;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", this.getId()).add("timestamp", this.getTimestamp()).add("reason", (Object)this.reason).add("directoryId", (Object)this.directoryId).toString();
    }
}

