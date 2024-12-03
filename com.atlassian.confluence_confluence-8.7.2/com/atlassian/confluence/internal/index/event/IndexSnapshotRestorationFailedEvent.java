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
import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@EventName(value="index_propagation_failed")
public class IndexSnapshotRestorationFailedEvent {
    private final String id;
    private final String nodeId;
    private final long propagationTime;
    private final IndexSnapshotError error;

    public IndexSnapshotRestorationFailedEvent(@Nullable String id, String nodeId, long propagationTime, IndexSnapshotError error) {
        this.id = id;
        this.nodeId = nodeId;
        this.propagationTime = propagationTime;
        this.error = Objects.requireNonNull(error);
    }

    public @Nullable String getId() {
        return this.id;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public IndexSnapshotError getError() {
        return this.error;
    }

    public long getPropagationTime() {
        return this.propagationTime;
    }
}

