/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.Schedule$Type
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.impl.schedule.caesium.TimeoutPolicy;
import com.atlassian.confluence.schedule.ScheduledJob;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.Schedule;
import java.util.Optional;

public interface ManagedScheduledJob
extends ScheduledJob {
    public JobId getJobId();

    public boolean isEditable();

    public boolean isKeepingHistory();

    public boolean canRunAdhoc();

    public boolean canDisable();

    public long getLockWaitTime();

    public boolean isLocalJob();

    public Optional<TimeoutPolicy> getTimeoutPolicy();

    public static boolean isCronJob(ManagedScheduledJob job) {
        return job.getJobConfig().getSchedule().getType() == Schedule.Type.CRON_EXPRESSION;
    }

    default public boolean disabledByDefault() {
        return false;
    }
}

