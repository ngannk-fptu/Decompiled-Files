/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.CheckForNull
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.RunDetails;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;

@PublicApi
public interface SchedulerHistoryService {
    @CheckForNull
    public RunDetails getLastRunForJob(JobId var1);

    @CheckForNull
    public RunDetails getLastSuccessfulRunForJob(JobId var1);

    public Map<JobId, RunDetails> getLastRunForJobs(List<JobId> var1);
}

