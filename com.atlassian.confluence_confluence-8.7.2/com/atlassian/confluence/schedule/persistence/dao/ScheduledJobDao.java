/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule.persistence.dao;

import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.scheduler.config.JobId;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ScheduledJobDao {
    public @Nullable ScheduledJobStatus getScheduledJobStatus(JobId var1);

    public void saveScheduledJobStatus(JobId var1, ScheduledJobStatus var2);

    public void updateStatus(JobId var1, ExecutionStatus var2);

    public void addHistory(JobId var1, @Nullable ScheduledJobHistory var2, Date var3);

    public void updateNextOccurrence(JobId var1, Date var2);

    public void saveScheduledJobConfiguration(JobId var1, ScheduledJobConfiguration var2);

    public @Nullable ScheduledJobConfiguration getScheduledJobConfiguration(JobId var1);
}

