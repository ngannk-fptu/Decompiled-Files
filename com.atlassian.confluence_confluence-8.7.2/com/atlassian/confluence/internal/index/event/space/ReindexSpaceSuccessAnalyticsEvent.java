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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AsynchronousPreferred
@EventName(value="reindex.space.success")
public class ReindexSpaceSuccessAnalyticsEvent
extends AbstractReindexAnalyticsEvent {
    private final int numSpaces;
    private final Set<Integer> spacesIds;

    public ReindexSpaceSuccessAnalyticsEvent(ReIndexJob reIndexJob) {
        super(reIndexJob);
        List<String> spaceKeys = reIndexJob.getSpaceKeys();
        this.numSpaces = spaceKeys.size();
        this.spacesIds = spaceKeys.stream().map(xva$0 -> Objects.hash(xva$0)).collect(Collectors.toSet());
    }

    public int getNumSpaces() {
        return this.numSpaces;
    }

    public Set<Integer> getSpacesIds() {
        return this.spacesIds;
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
        ReindexSpaceSuccessAnalyticsEvent that = (ReindexSpaceSuccessAnalyticsEvent)o;
        return this.numSpaces == that.numSpaces && this.spacesIds.equals(that.spacesIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.numSpaces, this.spacesIds);
    }
}

