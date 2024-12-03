/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.index.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexNodeStatus;
import com.atlassian.confluence.index.status.ReindexType;
import com.atlassian.confluence.internal.index.event.AbstractReindexAnalyticsEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AsynchronousPreferred
@EventName(value="reindex.failure")
public class ReindexFailureAnalyticsEvent
extends AbstractReindexAnalyticsEvent {
    private final Set<Integer> spacesIds;
    private final Set<Integer> failedNodeIds;
    private final Set<ReIndexError> reasons;
    private final ReindexType reindexType;

    public ReindexFailureAnalyticsEvent(ReIndexJob reIndexJob) {
        super(reIndexJob);
        this.spacesIds = reIndexJob.getSpaceKeys().stream().map(xva$0 -> Objects.hash(xva$0)).collect(Collectors.toSet());
        Collection<ReIndexNodeStatus> nodeStatuses = reIndexJob.getNodeStatuses();
        this.failedNodeIds = new HashSet<Integer>(nodeStatuses.size());
        this.reasons = new HashSet<ReIndexError>(nodeStatuses.size());
        nodeStatuses.stream().filter(ReIndexNodeStatus::isFailed).forEach(reIndexNodeStatus -> {
            this.failedNodeIds.add(Integer.parseUnsignedInt(reIndexNodeStatus.getNodeId(), 16));
            this.reasons.add(Optional.ofNullable(reIndexNodeStatus.getError()).orElse(ReIndexError.UNKNOWN));
        });
        this.reindexType = reIndexJob.isSiteReindex() ? ReindexType.SITE : ReindexType.SPACE;
    }

    public Set<Integer> getSpacesIds() {
        return this.spacesIds;
    }

    public Set<Integer> getFailedNodeIds() {
        return this.failedNodeIds;
    }

    public Set<ReIndexError> getReasons() {
        return this.reasons;
    }

    public ReindexType getReindexType() {
        return this.reindexType;
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
        ReindexFailureAnalyticsEvent that = (ReindexFailureAnalyticsEvent)o;
        return this.spacesIds.equals(that.spacesIds) && this.failedNodeIds.equals(that.failedNodeIds) && this.reasons.equals(that.reasons) && this.reindexType == that.reindexType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{super.hashCode(), this.spacesIds, this.failedNodeIds, this.reasons, this.reindexType});
    }
}

