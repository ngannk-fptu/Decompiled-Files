/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.caesium.impl.ImmutableClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJobDao;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MemoryClusteredJobDao
implements ClusteredJobDao {
    private static final int INITIAL_STORE_SIZE = 256;
    private final ConcurrentMap<JobId, ClusteredJob> store = new ConcurrentHashMap<JobId, ClusteredJob>(256);

    @Override
    @Nullable
    public Date getNextRunTime(JobId jobId) {
        ClusteredJob job = (ClusteredJob)this.store.get(jobId);
        return job != null ? job.getNextRunTime() : null;
    }

    @Override
    @Nullable
    public Long getVersion(JobId jobId) {
        ClusteredJob job = (ClusteredJob)this.store.get(jobId);
        return job != null ? Long.valueOf(job.getVersion()) : null;
    }

    @Override
    @Nullable
    public ClusteredJob find(JobId jobId) {
        return (ClusteredJob)this.store.get(jobId);
    }

    @Override
    @Nonnull
    public Collection<ClusteredJob> findByJobRunnerKey(JobRunnerKey jobRunnerKey) {
        ImmutableList.Builder jobs = ImmutableList.builder();
        for (ClusteredJob job : this.store.values()) {
            if (!job.getJobRunnerKey().equals((Object)jobRunnerKey)) continue;
            jobs.add((Object)job);
        }
        return jobs.build();
    }

    @Override
    @Nonnull
    public Collection<ClusteredJob> findByJobRunnerKeys(List<JobRunnerKey> jobRunnerKeys) {
        ImmutableList.Builder jobs = ImmutableList.builder();
        for (ClusteredJob job : this.store.values()) {
            if (!jobRunnerKeys.contains(job.getJobRunnerKey())) continue;
            jobs.add((Object)job);
        }
        return jobs.build();
    }

    @Override
    @Nonnull
    public Map<JobId, Date> refresh() {
        ImmutableMap.Builder jobs = ImmutableMap.builder();
        for (ClusteredJob job : this.store.values()) {
            Date nextRunTime = job.getNextRunTime();
            if (nextRunTime == null) continue;
            jobs.put((Object)job.getJobId(), (Object)nextRunTime);
        }
        return jobs.build();
    }

    @Override
    @Nonnull
    public Set<JobRunnerKey> findAllJobRunnerKeys() {
        ImmutableSet.Builder keys = ImmutableSet.builder();
        for (ClusteredJob job : this.store.values()) {
            keys.add((Object)job.getJobRunnerKey());
        }
        return keys.build();
    }

    @Override
    public boolean create(ClusteredJob clusteredJob) {
        return this.store.putIfAbsent(clusteredJob.getJobId(), clusteredJob) == null;
    }

    @Override
    public boolean updateNextRunTime(JobId jobId, @Nullable Date nextRunTime, long expectedVersion) {
        ClusteredJob existing = (ClusteredJob)this.store.get(jobId);
        if (existing == null) {
            return false;
        }
        ImmutableClusteredJob updated = ImmutableClusteredJob.builder(existing).version(existing.getVersion() + 1L).nextRunTime(nextRunTime).build();
        return this.store.replace(jobId, existing, updated);
    }

    @Override
    public boolean delete(JobId jobId) {
        return this.store.remove(jobId) != null;
    }
}

