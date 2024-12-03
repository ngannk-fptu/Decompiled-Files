/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.scheduler.config.JobId;
import java.util.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ScheduledJobManager {
    @Transactional(readOnly=true)
    public List<ScheduledJobStatus> getScheduledJobs();

    @Transactional(readOnly=true)
    public ScheduledJobStatus getScheduledJob(JobId var1);

    public Date updateCronJobSchedule(JobId var1, String var2);

    public Date updateSimpleJobSchedule(JobId var1, long var2);

    public void runNow(JobId var1);

    public void disable(JobId var1);

    public void enable(JobId var1);

    @Transactional(readOnly=true)
    public String getCronExpression(JobId var1);

    @Transactional(readOnly=true)
    public Long getRepeatInterval(JobId var1);
}

