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
@EventName(value="index_propagation_skipped")
public class IndexSnapshotRestorationSkippedEvent {
    private final String id;
    private final String nodeId;

    public IndexSnapshotRestorationSkippedEvent(@Nullable String id, String nodeId) {
        this.id = id;
        this.nodeId = nodeId;
    }

    public @Nullable String getId() {
        return this.id;
    }

    public String getNodeId() {
        return this.nodeId;
    }
}

