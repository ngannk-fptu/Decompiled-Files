/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.status;

import java.time.Instant;
import lombok.Generated;

public class PlanStatusDto {
    public final String id;
    public final String name;
    public final String cloudId;
    public final String activeStatus;
    public final String progress;
    public final long elapsed;
    public final Instant startTime;
    public final Instant endTime;

    @Generated
    PlanStatusDto(String id, String name, String cloudId, String activeStatus, String progress, long elapsed, Instant startTime, Instant endTime) {
        this.id = id;
        this.name = name;
        this.cloudId = cloudId;
        this.activeStatus = activeStatus;
        this.progress = progress;
        this.elapsed = elapsed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Generated
    public static PlanStatusDtoBuilder builder() {
        return new PlanStatusDtoBuilder();
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getActiveStatus() {
        return this.activeStatus;
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
    public static class PlanStatusDtoBuilder {
        @Generated
        private String id;
        @Generated
        private String name;
        @Generated
        private String cloudId;
        @Generated
        private String activeStatus;
        @Generated
        private String progress;
        @Generated
        private long elapsed;
        @Generated
        private Instant startTime;
        @Generated
        private Instant endTime;

        @Generated
        PlanStatusDtoBuilder() {
        }

        @Generated
        public PlanStatusDtoBuilder id(String id) {
            this.id = id;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder cloudId(String cloudId) {
            this.cloudId = cloudId;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder activeStatus(String activeStatus) {
            this.activeStatus = activeStatus;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder progress(String progress) {
            this.progress = progress;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder elapsed(long elapsed) {
            this.elapsed = elapsed;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        @Generated
        public PlanStatusDtoBuilder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        @Generated
        public PlanStatusDto build() {
            return new PlanStatusDto(this.id, this.name, this.cloudId, this.activeStatus, this.progress, this.elapsed, this.startTime, this.endTime);
        }

        @Generated
        public String toString() {
            return "PlanStatusDto.PlanStatusDtoBuilder(id=" + this.id + ", name=" + this.name + ", cloudId=" + this.cloudId + ", activeStatus=" + this.activeStatus + ", progress=" + this.progress + ", elapsed=" + this.elapsed + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ")";
        }
    }
}

