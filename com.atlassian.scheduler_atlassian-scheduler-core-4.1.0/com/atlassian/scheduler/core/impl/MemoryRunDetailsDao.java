/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 */
package com.atlassian.scheduler.core.impl;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemoryRunDetailsDao
implements RunDetailsDao {
    private final Cache<JobId, JobRecord> store;

    public MemoryRunDetailsDao() {
        this(30);
    }

    public MemoryRunDetailsDao(int daysToKeepIdleHistory) {
        this.store = CacheBuilder.newBuilder().expireAfterWrite((long)daysToKeepIdleHistory, TimeUnit.DAYS).build();
    }

    @Override
    public RunDetails getLastRunForJob(JobId jobId) {
        JobRecord jobRecord = (JobRecord)this.store.getIfPresent((Object)jobId);
        return jobRecord != null ? jobRecord.lastRun : null;
    }

    @Override
    public RunDetails getLastSuccessfulRunForJob(JobId jobId) {
        JobRecord jobRecord = (JobRecord)this.store.getIfPresent((Object)jobId);
        return jobRecord != null ? jobRecord.lastRun : null;
    }

    @Override
    public Map<JobId, RunDetails> getLastRunForJobs(List<JobId> jobIds) {
        return jobIds.stream().filter(jobId -> Objects.nonNull(this.getLastRunForJob((JobId)jobId))).collect(Collectors.toMap(Function.identity(), this::getLastRunForJob));
    }

    @Override
    public void addRunDetails(JobId jobId, RunDetails runDetails) {
        JobRecord jobRecord = runDetails.getRunOutcome() == RunOutcome.SUCCESS ? new JobRecord(runDetails, runDetails) : new JobRecord(runDetails, this.getLastSuccessfulRunForJob(jobId));
        this.store.put((Object)jobId, (Object)jobRecord);
    }

    static class JobRecord {
        final RunDetails lastRun;
        final RunDetails lastSuccessfulRun;

        JobRecord(RunDetails lastRun, RunDetails lastSuccessfulRun) {
            this.lastRun = lastRun;
            this.lastSuccessfulRun = lastSuccessfulRun;
        }
    }
}

