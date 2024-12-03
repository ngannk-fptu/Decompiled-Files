/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.index.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@EventName(value="index_propagation_success")
public class IndexSnapshotRestoredSuccessfullyEvent {
    private final String id;
    private final String nodeId;
    private final long propagationTime;

    public IndexSnapshotRestoredSuccessfullyEvent(@Nullable String id, String nodeId, long propagationTime) {
        this.id = id;
        this.nodeId = nodeId;
        this.propagationTime = propagationTime;
    }

    public @Nullable String getId() {
        return this.id;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public long getPropagationTime() {
        return this.propagationTime;
    }
}

