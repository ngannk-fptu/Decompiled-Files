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
@EventName(value="started_indexing")
public class ReIndexStartedAnalyticsEvent {
    private final String id;
    private final IndexingMethod method;

    private ReIndexStartedAnalyticsEvent(@Nullable String id, IndexingMethod method) {
        this.id = id;
        this.method = Objects.requireNonNull(method);
    }

    public @Nullable String getId() {
        return this.id;
    }

    public IndexingMethod getMethod() {
        return this.method;
    }

    public static ReIndexStartedAnalyticsEvent newIndexRecoveryReIndexStartedEvent() {
        return new ReIndexStartedAnalyticsEvent(null, IndexingMethod.INDEX_RECOVERY);
    }

    public static ReIndexStartedAnalyticsEvent newPropagationReIndexStartedEvent(String indexingId) {
        return new ReIndexStartedAnalyticsEvent(Objects.requireNonNull(indexingId), IndexingMethod.INDEX_PROPAGATION);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReIndexStartedAnalyticsEvent that = (ReIndexStartedAnalyticsEvent)o;
        return Objects.equals(this.id, that.id) && this.method == that.method;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.method});
    }

    public static enum IndexingMethod {
        INDEX_RECOVERY,
        INDEX_PROPAGATION;

    }
}

