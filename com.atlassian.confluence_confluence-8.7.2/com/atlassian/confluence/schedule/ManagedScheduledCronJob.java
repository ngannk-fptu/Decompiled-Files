/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.impl.schedule.caesium.TimeoutPolicy;
import com.atlassian.confluence.schedule.AbstractManagedScheduledJob;
import com.atlassian.confluence.schedule.ScheduledCronJob;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ManagedScheduledCronJob
extends AbstractManagedScheduledJob {
    public static ManagedScheduledCronJob disabledByDefault(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable) {
        return new ManagedScheduledCronJob(id, jobRunner, runOncePerCluster, cronExpression, editable, keepingHistory, canRunAdhoc, canDisable, false, 0L, null, true);
    }

    public ManagedScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable) {
        this(id, jobRunner, runOncePerCluster, cronExpression, editable, keepingHistory, canRunAdhoc, canDisable, false, 0L, null);
    }

    public ManagedScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly) {
        this(id, jobRunner, runOncePerCluster, cronExpression, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null);
    }

    public ManagedScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, @Nullable TimeoutPolicy timeoutPolicy) {
        super(id, jobRunner, ScheduledCronJob.toJobConfig(id, runOncePerCluster, cronExpression), editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy);
    }

    public ManagedScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, @Nullable TimeoutPolicy timeoutPolicy, boolean disabledByDefault) {
        super(id, jobRunner, ScheduledCronJob.toJobConfig(id, runOncePerCluster, cronExpression), editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy, disabledByDefault);
    }

    public ManagedScheduledCronJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, false, 0L, null, false);
    }

    public ManagedScheduledCronJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null, false);
    }

    public ManagedScheduledCronJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, boolean disabledByDefault) {
        this(jobId, jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, 0L, null, disabledByDefault);
    }

    public ManagedScheduledCronJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, @Nullable TimeoutPolicy timeoutPolicy) {
        super(jobId.toString(), jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy, false);
    }

    public ManagedScheduledCronJob(JobId jobId, JobRunner jobRunner, JobConfig jobConfig, boolean editable, boolean keepingHistory, boolean canRunAdhoc, boolean canDisable, boolean clusteredOnly, long lockWaitTime, @Nullable TimeoutPolicy timeoutPolicy, boolean disabledByDefault) {
        super(jobId.toString(), jobRunner, jobConfig, editable, keepingHistory, canRunAdhoc, canDisable, clusteredOnly, lockWaitTime, timeoutPolicy, disabledByDefault);
    }

    public String getDefaultCronExpression() {
        return this.getJobConfig().getSchedule().getCronScheduleInfo().getCronExpression();
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
        ManagedScheduledCronJob that = (ManagedScheduledCronJob)o;
        return this.getDefaultCronExpression().equals(that.getDefaultCronExpression());
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.getDefaultCronExpression().hashCode();
    }
}

