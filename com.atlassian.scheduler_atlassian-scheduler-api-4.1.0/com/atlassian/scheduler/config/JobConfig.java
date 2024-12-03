/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.config;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.util.Safe;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class JobConfig {
    static final Map<String, Serializable> NO_PARAMETERS = ImmutableMap.of();
    private final JobRunnerKey jobRunnerKey;
    private final RunMode runMode;
    private final Schedule schedule;
    private final Map<String, Serializable> parameters;

    public static JobConfig forJobRunnerKey(JobRunnerKey jobRunnerKey) {
        Objects.requireNonNull(jobRunnerKey, "jobRunnerKey");
        return new JobConfig(jobRunnerKey, RunMode.RUN_ONCE_PER_CLUSTER, Schedule.runOnce(null), NO_PARAMETERS);
    }

    private JobConfig(JobRunnerKey jobRunnerKey, @Nullable RunMode runMode, @Nullable Schedule schedule, Map<String, Serializable> parameters) {
        this.jobRunnerKey = jobRunnerKey;
        this.runMode = runMode != null ? runMode : RunMode.RUN_ONCE_PER_CLUSTER;
        this.schedule = schedule != null ? schedule : Schedule.runOnce(null);
        this.parameters = parameters;
    }

    @Nonnull
    public JobRunnerKey getJobRunnerKey() {
        return this.jobRunnerKey;
    }

    @Nonnull
    public RunMode getRunMode() {
        return this.runMode;
    }

    @Nonnull
    public Schedule getSchedule() {
        return this.schedule;
    }

    @Nonnull
    public Map<String, Serializable> getParameters() {
        return this.parameters;
    }

    public JobConfig withRunMode(RunMode runMode) {
        return new JobConfig(this.jobRunnerKey, runMode, this.schedule, this.parameters);
    }

    public JobConfig withSchedule(Schedule schedule) {
        return new JobConfig(this.jobRunnerKey, this.runMode, schedule, this.parameters);
    }

    public JobConfig withParameters(@Nullable Map<String, Serializable> parameters) {
        return new JobConfig(this.jobRunnerKey, this.runMode, this.schedule, Safe.copy(parameters));
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JobConfig other = (JobConfig)o;
        return this.jobRunnerKey.equals(other.jobRunnerKey) && this.runMode == other.runMode && this.schedule.equals(other.schedule) && this.parameters.equals(other.parameters);
    }

    public int hashCode() {
        int result = this.jobRunnerKey.hashCode();
        result = 31 * result + this.runMode.hashCode();
        result = 31 * result + this.schedule.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    public String toString() {
        return "JobConfig[jobRunnerKey=" + this.jobRunnerKey + ",runMode=" + (Object)((Object)this.runMode) + ",schedule=" + this.schedule + ",parameters=" + this.parameters + ']';
    }
}

