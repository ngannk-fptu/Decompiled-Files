/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunDetails
 *  javax.annotation.CheckForNull
 */
package com.atlassian.scheduler.core.impl;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.status.RunDetails;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;

public class NullRunDetailsDao
implements RunDetailsDao {
    @Override
    public RunDetails getLastRunForJob(JobId jobId) {
        return null;
    }

    @Override
    public RunDetails getLastSuccessfulRunForJob(JobId jobId) {
        return null;
    }

    @Override
    @CheckForNull
    public Map<JobId, RunDetails> getLastRunForJobs(List<JobId> jobIds) {
        return null;
    }

    @Override
    public void addRunDetails(JobId jobId, RunDetails runDetails) {
    }
}

