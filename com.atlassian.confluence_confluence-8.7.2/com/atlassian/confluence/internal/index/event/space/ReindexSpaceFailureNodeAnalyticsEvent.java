/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.index.event.space;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.internal.index.event.AbstractReindexAnalyticsEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;

@AsynchronousPreferred
@EventName(value="reindex.space.failure.node")
public class ReindexSpaceFailureNodeAnalyticsEvent
extends AbstractReindexAnalyticsEvent {
    private final int nodeId;
    private final ReIndexError reason;

    public ReindexSpaceFailureNodeAnalyticsEvent(ReIndexJob reIndexJob, int nodeId, ReIndexError reason) {
        super(reIndexJob);
        this.nodeId = nodeId;
        this.reason = reason;
    }

    public int getNodeId() {
        return this.nodeId;
    }

    public ReIndexError getReason() {
        return this.reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ReindexSpaceFailureNodeAnalyticsEvent that = (ReindexSpaceFailureNodeAnalyticsEvent)o;
        return this.nodeId == that.nodeId && this.reason == that.reason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{super.hashCode(), this.nodeId, this.reason});
    }
}

