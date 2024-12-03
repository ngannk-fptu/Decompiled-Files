/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.tombstone;

public class AbstractTombstone {
    private long id;
    private long timestamp;

    protected AbstractTombstone() {
    }

    protected AbstractTombstone(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return this.id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

