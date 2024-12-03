/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerHistoryService
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunDetails
 *  javax.annotation.CheckForNull
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.SchedulerHistoryService;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.status.RunDetails;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;

public class DefaultSchedulerHistoryService
implements SchedulerHistoryService {
    private final RunDetailsDao runDetailsDao;

    public DefaultSchedulerHistoryService(RunDetailsDao runDetailsDao) {
        this.runDetailsDao = runDetailsDao;
    }

    @CheckForNull
    public RunDetails getLastSuccessfulRunForJob(JobId jobId) {
        return this.runDetailsDao.getLastSuccessfulRunForJob(jobId);
    }

    @CheckForNull
    public Map<JobId, RunDetails> getLastRunForJobs(List<JobId> jobIds) {
        return this.runDetailsDao.getLastRunForJobs(jobIds);
    }

    @CheckForNull
    public RunDetails getLastRunForJob(JobId jobId) {
        return this.runDetailsDao.getLastRunForJob(jobId);
    }
}

