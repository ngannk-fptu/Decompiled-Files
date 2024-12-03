/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.status.JobDetails
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.scheduler.status.JobDetails;
import javax.annotation.Nonnull;

public interface ExportScheduleService {
    public void registerJobRunner();

    public void unregisterJobRunner();

    public JobDetails scheduleJob(@Nonnull ScheduleConfig var1);

    public void unscheduleJob();
}

