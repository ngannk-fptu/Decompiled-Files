/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.AbstractTombstone;

public abstract class ApplicationTombstone
extends AbstractTombstone {
    protected long applicationId;

    protected ApplicationTombstone() {
    }

    public ApplicationTombstone(long timestamp, long applicationId) {
        super(timestamp);
        this.applicationId = applicationId;
    }

    public long getApplicationId() {
        return this.applicationId;
    }

    protected void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }
}

