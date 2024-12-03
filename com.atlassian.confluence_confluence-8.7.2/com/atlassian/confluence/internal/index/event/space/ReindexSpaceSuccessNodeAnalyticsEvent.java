/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.index.event.space;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.internal.index.event.AbstractReindexAnalyticsEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;

@AsynchronousPreferred
@EventName(value="reindex.space.success.node")
public class ReindexSpaceSuccessNodeAnalyticsEvent
extends AbstractReindexAnalyticsEvent {
    private final int nodeId;

    public ReindexSpaceSuccessNodeAnalyticsEvent(ReIndexJob reIndexJob, int nodeId) {
        super(reIndexJob);
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return this.nodeId;
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
        ReindexSpaceSuccessNodeAnalyticsEvent that = (ReindexSpaceSuccessNodeAnalyticsEvent)o;
        return this.nodeId == that.nodeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.nodeId);
    }
}

