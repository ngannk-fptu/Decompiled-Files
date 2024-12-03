/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.caesium.spi.ClusteredJob
 *  com.atlassian.scheduler.caesium.spi.ClusteredJobDao
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJobDao;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerClusteredJobDaoProxyWrapper
implements ClusteredJobDao {
    private static final Logger log = LoggerFactory.getLogger(SchedulerClusteredJobDaoProxyWrapper.class);
    private final ClusteredJobDao proxy;

    public SchedulerClusteredJobDaoProxyWrapper(ClusteredJobDao proxy) {
        this.proxy = proxy;
    }

    public @Nullable Date getNextRunTime(JobId jobId) {
        return this.proxy.getNextRunTime(jobId);
    }

    public @Nullable Long getVersion(JobId jobId) {
        return this.proxy.getVersion(jobId);
    }

    public @Nullable ClusteredJob find(JobId jobId) {
        return this.proxy.find(jobId);
    }

    public @NonNull Collection<ClusteredJob> findByJobRunnerKey(JobRunnerKey jobRunnerKey) {
        return this.proxy.findByJobRunnerKey(jobRunnerKey);
    }

    public @NonNull Map<JobId, Date> refresh() {
        return this.proxy.refresh();
    }

    public @NonNull Set<JobRunnerKey> findAllJobRunnerKeys() {
        return this.proxy.findAllJobRunnerKeys();
    }

    public boolean create(ClusteredJob clusteredJob) {
        try {
            return this.proxy.create(clusteredJob);
        }
        catch (Exception e) {
            log.warn("Could not create clustered job '{}'", (Object)clusteredJob.getJobId(), (Object)e);
            return false;
        }
    }

    public boolean updateNextRunTime(JobId jobId, @Nullable Date nextRunTime, long expectedVersion) {
        try {
            return this.proxy.updateNextRunTime(jobId, nextRunTime, expectedVersion);
        }
        catch (Exception e) {
            log.warn("Could not update next run time for clustered job '{}'", (Object)jobId, (Object)e);
            return false;
        }
    }

    public boolean delete(JobId jobId) {
        try {
            return this.proxy.delete(jobId);
        }
        catch (Exception e) {
            log.warn("Could not delete clustered job '{}'", (Object)jobId, (Object)e);
            return false;
        }
    }
}

