/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.impl.schedule.caesium.TimeoutPolicy;
import com.atlassian.confluence.schedule.AbstractManagedScheduledJob;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.ScheduledSimpleJob;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;

public class ManagedScheduledSimpleJob
extends AbstractManagedScheduledJob {
    public ManagedScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable) {
        this(id, jobRunner, runOncePerCluster, repeatInterval, -1, editable, keepingHistory, canRunAdhoc, canDisable, false, 0L, null);
    }

    public ManagedScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, int repeatCount, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable) {
        this(id, jobRunner, runOncePerCluster, repeatInterval, repeatCount, editable, keepingHistory, canRunAdhoc, canDisable, false, 0L, null);
    }

    public ManagedScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly) {
        this(id, jobRunner, runOncePerCluster, repeatInterval, -1, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null);
    }

    public ManagedScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, TimeoutPolicy timeoutPolicy) {
        this(id, jobRunner, runOncePerCluster, repeatInterval, -1, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy);
    }

    public ManagedScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, int repeatCount, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly) {
        this(id, jobRunner, runOncePerCluster, repeatInterval, repeatCount, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null);
    }

    public ManagedScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, int repeatCount, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, TimeoutPolicy timeoutPolicy) {
        super(id, jobRunner, ScheduledSimpleJob.toJobConfig(id, runOncePerCluster, repeatInterval, repeatCount), editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy);
    }

    public ManagedScheduledSimpleJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, false, 0L, null, false);
    }

    public ManagedScheduledSimpleJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null, false);
    }

    public ManagedScheduledSimpleJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, boolean disabledByDefault) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null, disabledByDefault);
    }

    public ManagedScheduledSimpleJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, TimeoutPolicy timeoutPolicy) {
        super(jobId.toString(), jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy, false);
    }

    public ManagedScheduledSimpleJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, TimeoutPolicy timeoutPolicy, boolean disabledByDefault) {
        super(jobId.toString(), jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy, disabledByDefault);
    }

    public Long getDefaultRepeatInterval() {
        return this.getJobConfig().getSchedule().getIntervalScheduleInfo().getIntervalInMillis();
    }

    public Integer getDefaultRepeatCount() {
        return ScheduleUtil.getRepeatCount(this.getJobConfig().getParameters());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ManagedScheduledSimpleJob that = (ManagedScheduledSimpleJob)o;
        return this.getDefaultRepeatInterval().equals(that.getDefaultRepeatInterval()) && this.getDefaultRepeatCount().equals(that.getDefaultRepeatCount());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.getDefaultRepeatInterval().hashCode();
        result = 31 * result + this.getDefaultRepeatCount();
        return result;
    }
}

