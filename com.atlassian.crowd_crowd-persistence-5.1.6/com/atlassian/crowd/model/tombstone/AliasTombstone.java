/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.ApplicationTombstone;

public class AliasTombstone
extends ApplicationTombstone {
    private String username;

    protected AliasTombstone() {
    }

    public AliasTombstone(long timestamp, long applicationId, String username) {
        super(timestamp, applicationId);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }
}

