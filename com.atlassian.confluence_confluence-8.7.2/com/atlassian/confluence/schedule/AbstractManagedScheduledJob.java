/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.RunMode
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.impl.schedule.caesium.TimeoutPolicy;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.RunMode;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractManagedScheduledJob
implements ManagedScheduledJob {
    public static final long DEFAULT_LOCK_WAIT_TIME_MS = 30000L;
    public static final TimeoutPolicy DEFAULT_TIMEOUT_POLICY = TimeoutPolicy.RUN_ON_TIMEOUT;
    public static final String LOCK_WAIT_TIME_PARAMETER_NAME = "com.atlassian.confluence.schedule.AbstractManagedScheduledJob.lock_wait_time";
    public static final String TIMEOUT_POLICY_PARAMETER_NAME = "com.atlassian.confluence.schedule.AbstractManagedScheduledJob.timeout_policy";
    private final JobId jobId;
    private final JobRunner jobRunner;
    private final JobConfig jobConfig;
    private final boolean editable;
    private final boolean keepingHistory;
    private final boolean canRunAdhoc;
    private final boolean canDisable;
    private final boolean clusteredOnly;
    private final boolean isLocalJob;
    private final TimeoutPolicy timeoutPolicy;
    private final long lockWaitTime;
    private final boolean disabledByDefault;

    protected AbstractManagedScheduledJob(String jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null);
    }

    protected AbstractManagedScheduledJob(String jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, @Nullable TimeoutPolicy timeoutPolicy) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy, false);
    }

    protected AbstractManagedScheduledJob(String jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, @Nullable TimeoutPolicy timeoutPolicy, boolean disabledByDefault) {
        this.jobId = JobId.of((String)jobId);
        this.jobRunner = jobRunner;
        this.jobConfig = jobConfig;
        this.editable = editable;
        this.keepingHistory = keepingHistory;
        this.canRunAdhoc = canRunAdhoc;
        this.canDisable = canDisable;
        this.clusteredOnly = clusteredOnly;
        this.isLocalJob = !this.jobConfig.getRunMode().equals((Object)RunMode.RUN_ONCE_PER_CLUSTER);
        this.lockWaitTime = AbstractManagedScheduledJob.requireNonNegative(lockWaitTime);
        this.timeoutPolicy = timeoutPolicy;
        this.disabledByDefault = disabledByDefault;
    }

    @Override
    public JobId getJobId() {
        return this.jobId;
    }

    @Override
    public JobRunner getJobRunner() {
        return this.jobRunner;
    }

    @Override
    public JobConfig getJobConfig() {
        return this.jobConfig;
    }

    @Override
    public boolean isEditable() {
        return this.editable;
    }

    @Override
    public boolean isKeepingHistory() {
        return this.keepingHistory;
    }

    @Override
    public boolean canRunAdhoc() {
        return this.canRunAdhoc;
    }

    @Override
    public boolean canDisable() {
        return this.canDisable;
    }

    @Override
    public boolean isClusteredOnly() {
        return this.clusteredOnly;
    }

    @Override
    public boolean isLocalJob() {
        return this.isLocalJob;
    }

    @Override
    public long getLockWaitTime() {
        return this.lockWaitTime;
    }

    @Override
    public Optional<TimeoutPolicy> getTimeoutPolicy() {
        return Optional.ofNullable(this.timeoutPolicy);
    }

    @Override
    public boolean disabledByDefault() {
        return this.disabledByDefault;
    }

    public String toString() {
        ToStringBuilder toString = new ToStringBuilder((Object)this);
        toString.append("jobId", (Object)this.jobId);
        toString.append("jobRunner", (Object)this.jobRunner);
        toString.append("jobConfig", (Object)this.jobConfig);
        toString.append("editable", this.editable);
        toString.append("keepingHistory", this.keepingHistory);
        toString.append("canRunAdhoc", this.canRunAdhoc);
        toString.append("canDisable", this.canDisable);
        toString.append("clusteredOnly", this.clusteredOnly);
        toString.append("isLocalJob", this.isLocalJob);
        toString.append("lockWaitTime", this.lockWaitTime);
        if (this.timeoutPolicy != null) {
            toString.append("timeoutPolicy", (Object)this.timeoutPolicy);
        }
        return toString.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractManagedScheduledJob that = (AbstractManagedScheduledJob)o;
        if (this.editable != that.editable) {
            return false;
        }
        if (this.keepingHistory != that.keepingHistory) {
            return false;
        }
        if (this.canRunAdhoc != that.canRunAdhoc) {
            return false;
        }
        if (this.canDisable != that.canDisable) {
            return false;
        }
        if (this.clusteredOnly != that.clusteredOnly) {
            return false;
        }
        if (this.isLocalJob != that.isLocalJob) {
            return false;
        }
        if (this.lockWaitTime != that.lockWaitTime) {
            return false;
        }
        if (!Objects.equals(this.jobId, that.jobId)) {
            return false;
        }
        if (!Objects.equals(this.jobRunner, that.jobRunner)) {
            return false;
        }
        if (!Objects.equals(this.jobConfig, that.jobConfig)) {
            return false;
        }
        return this.timeoutPolicy == that.timeoutPolicy;
    }

    public int hashCode() {
        int result = this.jobId != null ? this.jobId.hashCode() : 0;
        result = 31 * result + (this.jobRunner != null ? this.jobRunner.hashCode() : 0);
        result = 31 * result + (this.jobConfig != null ? this.jobConfig.hashCode() : 0);
        result = 31 * result + (this.editable ? 1 : 0);
        result = 31 * result + (this.keepingHistory ? 1 : 0);
        result = 31 * result + (this.canRunAdhoc ? 1 : 0);
        result = 31 * result + (this.canDisable ? 1 : 0);
        result = 31 * result + (this.clusteredOnly ? 1 : 0);
        result = 31 * result + (this.isLocalJob ? 1 : 0);
        result = 31 * result + (this.timeoutPolicy != null ? this.timeoutPolicy.hashCode() : 0);
        result = 31 * result + (int)(this.lockWaitTime ^ this.lockWaitTime >>> 32);
        return result;
    }

    private static long requireNonNegative(long val) {
        if (val < 0L) {
            throw new IllegalArgumentException("lockWaitTime should be a positive number, negative value is provided instead (" + val + ")");
        }
        return val;
    }
}

