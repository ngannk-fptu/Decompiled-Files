/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.status;

import java.time.Instant;
import lombok.Generated;

public class StepStatusDto {
    public final String type;
    public final String progress;
    public final long elapsed;
    public final Instant startTime;
    public final Instant endTime;

    @Generated
    StepStatusDto(String type, String progress, long elapsed, Instant startTime, Instant endTime) {
        this.type = type;
        this.progress = progress;
        this.elapsed = elapsed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Generated
    public static StepStatusDtoBuilder builder() {
        return new StepStatusDtoBuilder();
    }

    @Generated
    public String getType() {
        return this.type;
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
    public static class StepStatusDtoBuilder {
        @Generated
        private String type;
        @Generated
        private String progress;
        @Generated
        private long elapsed;
        @Generated
        private Instant startTime;
        @Generated
        private Instant endTime;

        @Generated
        StepStatusDtoBuilder() {
        }

        @Generated
        public StepStatusDtoBuilder type(String type) {
            this.type = type;
            return this;
        }

        @Generated
        public StepStatusDtoBuilder progress(String progress) {
            this.progress = progress;
            return this;
        }

        @Generated
        public StepStatusDtoBuilder elapsed(long elapsed) {
            this.elapsed = elapsed;
            return this;
        }

        @Generated
        public StepStatusDtoBuilder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        @Generated
        public StepStatusDtoBuilder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        @Generated
        public StepStatusDto build() {
            return new StepStatusDto(this.type, this.progress, this.elapsed, this.startTime, this.endTime);
        }

        @Generated
        public String toString() {
            return "StepStatusDto.StepStatusDtoBuilder(type=" + this.type + ", progress=" + this.progress + ", elapsed=" + this.elapsed + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ")";
        }
    }
}

