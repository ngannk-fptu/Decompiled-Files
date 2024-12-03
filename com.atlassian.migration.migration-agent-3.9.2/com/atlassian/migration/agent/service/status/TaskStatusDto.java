/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.status;

import java.time.Instant;
import lombok.Generated;

public class TaskStatusDto {
    public final String name;
    public final String taskType;
    public final String spaceKey;
    public final String status;
    public final String progress;
    public final long elapsed;
    public final Instant startTime;
    public final Instant endTime;

    @Generated
    TaskStatusDto(String name, String taskType, String spaceKey, String status, String progress, long elapsed, Instant startTime, Instant endTime) {
        this.name = name;
        this.taskType = taskType;
        this.spaceKey = spaceKey;
        this.status = status;
        this.progress = progress;
        this.elapsed = elapsed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Generated
    public static TaskStatusDtoBuilder builder() {
        return new TaskStatusDtoBuilder();
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getTaskType() {
        return this.taskType;
    }

    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public String getProgress() {
        return this.progress;
    }

    @Generated
    public long getElapsed() {
        return this.elapsed;
    }

    @Generated
    public Instant getStartTime() {
        return this.startTime;
    }

    @Generated
    public Instant getEndTime() {
        return this.endTime;
    }

    @Generated
    public static class TaskStatusDtoBuilder {
        @Generated
        private String name;
        @Generated
        private String taskType;
        @Generated
        private String spaceKey;
        @Generated
        private String status;
        @Generated
        private String progress;
        @Generated
        private long elapsed;
        @Generated
        private Instant startTime;
        @Generated
        private Instant endTime;

        @Generated
        TaskStatusDtoBuilder() {
        }

        @Generated
        public TaskStatusDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder taskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder progress(String progress) {
            this.progress = progress;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder elapsed(long elapsed) {
            this.elapsed = elapsed;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        @Generated
        public TaskStatusDtoBuilder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        @Generated
        public TaskStatusDto build() {
            return new TaskStatusDto(this.name, this.taskType, this.spaceKey, this.status, this.progress, this.elapsed, this.startTime, this.endTime);
        }

        @Generated
        public String toString() {
            return "TaskStatusDto.TaskStatusDtoBuilder(name=" + this.name + ", taskType=" + this.taskType + ", spaceKey=" + this.spaceKey + ", status=" + this.status + ", progress=" + this.progress + ", elapsed=" + this.elapsed + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ")";
        }
    }
}

