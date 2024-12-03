/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.collections4.ListUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.index.status;

import com.atlassian.confluence.index.status.ReIndexNodeStatus;
import com.atlassian.confluence.index.status.ReIndexStage;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ReIndexJob
implements Serializable {
    private static final long serialVersionUID = 5942696706294809627L;
    private final String id = UUID.randomUUID().toString();
    private Instant startTime;
    private Instant finishTime;
    private ReIndexStage stage;
    private boolean acknowledged;
    private Progress rebuildingProgress;
    private Instant lastRebuildingUpdate;
    private Progress propagatingProgress;
    private Collection<ReIndexNodeStatus> nodeStatuses;
    private ConfluenceUser createdBy;
    private final List<String> spaceKeys;

    public ReIndexJob() {
        this(Instant.now(), 0L, Collections.emptyList());
    }

    public ReIndexJob(Instant startTime, long totalCount) {
        this(startTime, totalCount, Collections.emptyList());
    }

    public ReIndexJob(List<String> spaceKeys) {
        this(Instant.now(), 0L, spaceKeys);
    }

    public ReIndexJob(Instant startTime, long totalCount, List<String> spaceKeys) {
        this.startTime = Objects.requireNonNull(startTime);
        this.lastRebuildingUpdate = startTime;
        this.stage = ReIndexStage.REBUILDING;
        this.acknowledged = false;
        this.rebuildingProgress = new Progress(0L, totalCount);
        this.nodeStatuses = new ArrayList<ReIndexNodeStatus>();
        this.createdBy = AuthenticatedUserThreadLocal.get();
        this.spaceKeys = ListUtils.emptyIfNull(spaceKeys);
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public @Nullable Instant getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(Instant finishTime) {
        this.finishTime = finishTime;
    }

    public ReIndexStage getStage() {
        return this.stage;
    }

    public void setStage(ReIndexStage stage) {
        this.stage = stage;
    }

    public boolean isAcknowledged() {
        return this.acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public Progress getRebuildingProgress() {
        return this.rebuildingProgress;
    }

    public void setRebuildingProgress(Progress rebuildingProgress) {
        this.rebuildingProgress = rebuildingProgress;
    }

    public @Nullable Progress getPropagatingProgress() {
        return this.propagatingProgress;
    }

    public void setPropagatingProgress(Progress propagatingProgress) {
        this.propagatingProgress = propagatingProgress;
    }

    public Collection<ReIndexNodeStatus> getNodeStatuses() {
        return this.nodeStatuses;
    }

    public void setNodeStatuses(Collection<ReIndexNodeStatus> nodeStatuses) {
        this.nodeStatuses = Objects.requireNonNull(nodeStatuses);
    }

    public Instant getLastRebuildingUpdate() {
        return this.lastRebuildingUpdate;
    }

    public void setLastRebuildingUpdate(Instant lastRebuildingUpdate) {
        this.lastRebuildingUpdate = Objects.requireNonNull(lastRebuildingUpdate);
    }

    public String getId() {
        return this.id;
    }

    public ConfluenceUser getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(ConfluenceUser createdBy) {
        this.createdBy = createdBy;
    }

    public Duration getDuration() {
        return this.startTime == null ? Duration.ZERO : Duration.between(this.startTime, (Temporal)ObjectUtils.defaultIfNull((Object)this.finishTime, (Object)Instant.now()));
    }

    public List<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public boolean isSiteReindex() {
        return CollectionUtils.isEmpty(this.spaceKeys);
    }

    public boolean isComplete() {
        return this.stage == ReIndexStage.COMPLETE;
    }

    public boolean isFailed() {
        return this.stage == ReIndexStage.REBUILD_FAILED || this.stage == ReIndexStage.PROPAGATION_FAILED;
    }

    public static class Progress
    implements Serializable {
        private static final long serialVersionUID = 6047136180520551259L;
        private long total;
        private long processed;

        public Progress() {
            this(0L, 0L);
        }

        public Progress(long processed, long total) {
            this.processed = processed;
            this.total = total;
        }

        public long getTotal() {
            return this.total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getProcessed() {
            return this.processed;
        }

        public void setProcessed(long processed) {
            this.processed = processed;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Progress progress = (Progress)o;
            return this.total == progress.total && this.processed == progress.processed;
        }

        public int hashCode() {
            return Objects.hash(this.total, this.processed);
        }

        public long getPercentage() {
            return this.total > 0L ? this.processed * 100L / this.total : 0L;
        }
    }
}

