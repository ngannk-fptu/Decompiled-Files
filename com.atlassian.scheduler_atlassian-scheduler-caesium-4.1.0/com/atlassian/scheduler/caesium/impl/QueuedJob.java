/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.config.JobId;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class QueuedJob
implements Serializable,
Comparable<QueuedJob> {
    private static final long serialVersionUID = -3588231346686548223L;
    private final JobId jobId;
    private final long deadline;

    QueuedJob(@Nonnull JobId jobId, long deadline) {
        this.jobId = Objects.requireNonNull(jobId, "jobId");
        this.deadline = deadline;
        Preconditions.checkArgument((deadline >= 0L ? 1 : 0) != 0, (Object)"deadline cannot be negative");
    }

    public JobId getJobId() {
        return this.jobId;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public boolean equals(@Nullable Object o) {
        return this == o || o instanceof QueuedJob && this.equals((QueuedJob)o);
    }

    private boolean equals(QueuedJob other) {
        return this.deadline == other.deadline && this.jobId.equals((Object)other.jobId);
    }

    public int hashCode() {
        return 31 * this.jobId.hashCode() + (int)(this.deadline ^ this.deadline >>> 32);
    }

    @Override
    public int compareTo(QueuedJob other) {
        if (this.deadline < other.deadline) {
            return -1;
        }
        if (this.deadline > other.deadline) {
            return 1;
        }
        return this.jobId.compareTo(other.jobId);
    }

    public String toString() {
        return "QueuedJob[jobId=" + this.jobId + ",deadline=" + this.deadline + ']';
    }
}

