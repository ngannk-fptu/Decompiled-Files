/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.event;

import com.atlassian.confluence.index.status.ReIndexJob;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractReindexAnalyticsEvent {
    private final String runId;
    private final long duration;

    protected AbstractReindexAnalyticsEvent(ReIndexJob reIndexJob) {
        this.runId = reIndexJob.getId();
        Duration eventDuration = Optional.ofNullable(reIndexJob.getDuration()).orElse(Duration.between(reIndexJob.getStartTime(), Instant.now()));
        this.duration = eventDuration.toSeconds();
    }

    public String getRunId() {
        return this.runId;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractReindexAnalyticsEvent that = (AbstractReindexAnalyticsEvent)o;
        return this.duration == that.duration && this.runId.equals(that.runId);
    }

    public int hashCode() {
        return Objects.hash(this.runId, this.duration);
    }
}

