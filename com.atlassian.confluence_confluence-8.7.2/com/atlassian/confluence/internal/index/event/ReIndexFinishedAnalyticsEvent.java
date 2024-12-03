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
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@EventName(value="finished_indexing")
public class ReIndexFinishedAnalyticsEvent {
    private final String id;
    private final long indexingTime;
    private final int totalNodes;
    private final long contentIndexSize;
    private final long changeIndexSize;

    private ReIndexFinishedAnalyticsEvent(@Nullable String id, long indexingTime, int totalNodes, long contentIndexSize, long changeIndexSize) {
        this.id = id;
        this.indexingTime = indexingTime;
        this.totalNodes = totalNodes;
        this.contentIndexSize = contentIndexSize;
        this.changeIndexSize = changeIndexSize;
    }

    public static ReIndexFinishedAnalyticsEvent newIndexRecoveryReIndexFinishedEvent(long contentIndexSize, long changeIndexSize) {
        return new ReIndexFinishedAnalyticsEvent(null, 0L, 0, contentIndexSize, changeIndexSize);
    }

    public static ReIndexFinishedAnalyticsEvent newPropagationReIndexFinishedEvent(String indexingId, long indexingTime, int totalNodes, long contentIndexSize, long changeIndexSize) {
        return new ReIndexFinishedAnalyticsEvent(Objects.requireNonNull(indexingId), indexingTime, totalNodes, contentIndexSize, changeIndexSize);
    }

    public @Nullable String getId() {
        return this.id;
    }

    public long getIndexingTime() {
        return this.indexingTime;
    }

    public int getTotalNodes() {
        return this.totalNodes;
    }

    public long getContentIndexSize() {
        return this.contentIndexSize;
    }

    public long getChangeIndexSize() {
        return this.changeIndexSize;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReIndexFinishedAnalyticsEvent that = (ReIndexFinishedAnalyticsEvent)o;
        return this.totalNodes == that.totalNodes && this.contentIndexSize == that.contentIndexSize && this.changeIndexSize == that.changeIndexSize && Objects.equals(this.id, that.id);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.totalNodes, this.contentIndexSize, this.changeIndexSize);
    }
}

