/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Progress;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProgressDto {
    @JsonProperty
    private int completionPercent;
    @JsonProperty
    private Status status;
    @JsonProperty
    private Instant startTime;
    @JsonProperty
    private Instant endTime;
    @JsonProperty
    private String detailedStatus;
    @JsonProperty
    private String message;

    public ProgressDto(int completionPercent, Status status, String message, Instant startTime, Instant endTime, String detailedStatus) {
        this.completionPercent = completionPercent;
        this.status = status;
        this.message = message;
        this.startTime = startTime;
        this.endTime = endTime;
        this.detailedStatus = detailedStatus;
    }

    public ProgressDto(int completionPercent, Status status, String message, String detailedStatus) {
        this.completionPercent = completionPercent;
        this.status = status;
        this.message = message;
        this.detailedStatus = detailedStatus;
    }

    public int getCompletionPercent() {
        return this.completionPercent;
    }

    public String getMessage() {
        return this.message;
    }

    public String getDetailedStatus() {
        return this.detailedStatus;
    }

    public Status getStatus() {
        return this.status;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public static ProgressDto fromPlanEntity(Progress progress) {
        Objects.requireNonNull(progress);
        return ProgressDto.fromEntity(progress, null, false);
    }

    public static ProgressDto fromTaskEntity(Progress progress, ExecutionStatus planStatus) {
        return ProgressDto.fromTaskEntity(progress, planStatus, false);
    }

    public static ProgressDto fromTaskEntity(Progress progress, ExecutionStatus planStatus, boolean omitStartAndEndTime) {
        Objects.requireNonNull(progress);
        Objects.requireNonNull(planStatus);
        return ProgressDto.fromEntity(progress, planStatus, omitStartAndEndTime);
    }

    public static ProgressDto fromStatus(String status) {
        Optional<ExecutionStatus> maybeExecutionStatus = ProgressDto.stringToStatus(status);
        return maybeExecutionStatus.map(executionStatus -> new ProgressDto(0, ProgressDto.convertStatus(executionStatus, null), null, null, null, null)).orElse(null);
    }

    static Optional<ExecutionStatus> stringToStatus(String value) {
        if (value == null || "NOT_IN_ANY_PLAN".equals(value)) {
            return Optional.empty();
        }
        if ("MIGRATED".equals(value)) {
            return Optional.of(ExecutionStatus.DONE);
        }
        if ("QUEUED".equals(value)) {
            return Optional.of(ExecutionStatus.CREATED);
        }
        return Optional.of(ExecutionStatus.valueOf(value));
    }

    private static ProgressDto fromEntity(Progress progress, @Nullable ExecutionStatus parentStatus, boolean omitStartAndEndTime) {
        return new ProgressDto(progress.getPercent(), ProgressDto.convertStatus(progress.getStatus(), parentStatus), progress.getMessage(), omitStartAndEndTime ? null : (Instant)progress.getStartTime().orElse(null), omitStartAndEndTime ? null : (Instant)progress.getEndTime().orElse(null), progress.getDetailedStatus());
    }

    public static Status convertStatus(ExecutionStatus execStatus, @Nullable ExecutionStatus parentStatus) {
        switch (execStatus) {
            case CREATED: {
                return Status.READY;
            }
            case DONE: {
                return Status.FINISHED;
            }
            case FAILED: {
                return Status.FAILED;
            }
            case STOPPED: {
                return Status.STOPPED;
            }
            case VALIDATING: 
            case RUNNING: {
                return parentStatus == ExecutionStatus.STOPPING ? Status.STOPPING : Status.RUNNING;
            }
            case STOPPING: {
                return Status.STOPPING;
            }
            case INCOMPLETE: {
                return Status.INCOMPLETE;
            }
        }
        throw new IllegalArgumentException("Unknown execution status " + execStatus.name());
    }

    public static enum Status {
        READY,
        RUNNING,
        STOPPING,
        FINISHED,
        STOPPED,
        FAILED,
        INCOMPLETE;


        public static Set<Status> finishedStatus() {
            return EnumSet.of(STOPPED, FINISHED, FAILED, INCOMPLETE);
        }
    }
}

