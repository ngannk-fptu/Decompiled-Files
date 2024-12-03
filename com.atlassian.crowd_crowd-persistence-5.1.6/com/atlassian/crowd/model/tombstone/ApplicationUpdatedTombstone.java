/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.tombstone;

import com.atlassian.crowd.model.tombstone.ApplicationTombstone;

public class ApplicationUpdatedTombstone
extends ApplicationTombstone {
    protected ApplicationUpdatedTombstone() {
    }

    public ApplicationUpdatedTombstone(long timestamp, long applicationId) {
        super(timestamp, applicationId);
    }
}

