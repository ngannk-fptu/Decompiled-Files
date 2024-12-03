/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.confluence.impl.schedule.managers;

import com.atlassian.scheduler.config.JobId;
import java.util.Date;

public interface ScheduledJobNodeManager {
    public Date updateCronSchedule(JobId var1, String var2);

    public Date updateSimpleSchedule(JobId var1, long var2);

    public void disableJob(JobId var1);

    public void enableJob(JobId var1);
}

