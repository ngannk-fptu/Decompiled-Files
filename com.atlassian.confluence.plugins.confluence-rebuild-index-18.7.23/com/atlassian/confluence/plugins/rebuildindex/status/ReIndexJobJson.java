/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.index.status.ReIndexJob
 *  com.atlassian.confluence.index.status.ReIndexJob$Progress
 *  com.atlassian.confluence.index.status.ReIndexStage
 *  com.atlassian.confluence.index.status.ReindexType
 *  org.apache.commons.collections4.CollectionUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.plugins.rebuildindex.status;

import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexStage;
import com.atlassian.confluence.index.status.ReindexType;
import com.atlassian.confluence.plugins.rebuildindex.status.ReIndexNodeStatusJson;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ReIndexJobJson {
    private final String id;
    private final Long startTime;
    private final Long finishTime;
    private final Long duration;
    private final ReIndexStage stage;
    private final boolean acknowledged;
    private final long progress;
    private final Collection<ReIndexNodeStatusJson> nodeStatuses;
    private String createdBy;
    private ReindexType type;

    @JsonCreator
    public ReIndexJobJson(@JsonProperty(value="id") String id, @JsonProperty(value="startTime") Long startTime, @JsonProperty(value="completeTime") Long finishTime, @JsonProperty(value="duration") Long duration, @JsonProperty(value="stage") ReIndexStage stage, @JsonProperty(value="acknowledged") boolean acknowledged, @JsonProperty(value="progress") long progress, @JsonProperty(value="nodes") Collection<ReIndexNodeStatusJson> nodeStatuses, @JsonProperty(value="createdBy") String createdBy, @JsonProperty(value="type") ReindexType type) {
        this.id = id;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.duration = duration;
        this.stage = stage;
        this.acknowledged = acknowledged;
        this.progress = progress;
        this.nodeStatuses = new ArrayList<ReIndexNodeStatusJson>(nodeStatuses);
        this.createdBy = createdBy;
        this.type = type;
    }

    public ReIndexJobJson(ReIndexJob reIndexJob) {
        this.id = reIndexJob.getId();
        this.startTime = reIndexJob.getStartTime().getEpochSecond();
        Instant finishTime = reIndexJob.getFinishTime();
        this.finishTime = finishTime != null ? Long.valueOf(finishTime.getEpochSecond()) : null;
        Duration duration = reIndexJob.getDuration();
        this.duration = duration.toSeconds();
        this.stage = reIndexJob.getStage();
        this.acknowledged = reIndexJob.isAcknowledged();
        Optional.ofNullable(reIndexJob.getCreatedBy()).ifPresent(user -> {
            this.createdBy = user.getFullName();
        });
        ReIndexJob.Progress rebuildProgress = Optional.ofNullable(reIndexJob.getRebuildingProgress()).map(progress -> new ReIndexJob.Progress(progress.getProcessed(), progress.getTotal())).orElse(new ReIndexJob.Progress());
        ReIndexJob.Progress propagateProgress = Optional.ofNullable(reIndexJob.getPropagatingProgress()).map(progress -> new ReIndexJob.Progress(progress.getProcessed(), progress.getTotal())).orElse(new ReIndexJob.Progress());
        switch (this.stage) {
            case REBUILDING: {
                this.progress = rebuildProgress.getTotal() > 0L ? rebuildProgress.getProcessed() * 100L / rebuildProgress.getTotal() / 2L : 0L;
                break;
            }
            case PROPAGATING: {
                this.progress = propagateProgress.getTotal() > 0L ? 50L + propagateProgress.getProcessed() * 50L / propagateProgress.getTotal() : 0L;
                break;
            }
            case COMPLETE: {
                this.progress = 100L;
                break;
            }
            default: {
                this.progress = 0L;
            }
        }
        this.nodeStatuses = reIndexJob.getNodeStatuses().stream().map(ReIndexNodeStatusJson::new).collect(Collectors.toList());
        this.type = CollectionUtils.isEmpty((Collection)reIndexJob.getSpaceKeys()) ? ReindexType.SITE : ReindexType.SPACE;
    }

    @JsonProperty(value="id")
    public String getId() {
        return this.id;
    }

    @JsonProperty(value="startTime")
    public Long getStartTime() {
        return this.startTime;
    }

    @JsonProperty(value="completeTime")
    public @Nullable Long getFinishTime() {
        return this.finishTime;
    }

    @JsonProperty(value="duration")
    public Long getDuration() {
        return this.duration;
    }

    @JsonProperty(value="stage")
    public ReIndexStage getStage() {
        return this.stage;
    }

    @JsonProperty(value="acknowledged")
    public boolean isAcknowledged() {
        return this.acknowledged;
    }

    @JsonProperty(value="progress")
    public long getProgress() {
        return this.progress;
    }

    @JsonProperty(value="nodes")
    public Collection<ReIndexNodeStatusJson> getNodeStatuses() {
        return this.nodeStatuses;
    }

    @JsonProperty(value="createdBy")
    public String getCreatedBy() {
        return this.createdBy;
    }

    @JsonProperty(value="type")
    public ReindexType getType() {
        return this.type;
    }
}

