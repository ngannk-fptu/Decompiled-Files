/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.spi;

import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClusteredJobDao {
    @Nullable
    public Date getNextRunTime(JobId var1);

    @Nullable
    public Long getVersion(JobId var1);

    @Nullable
    public ClusteredJob find(JobId var1);

    @Nonnull
    public Collection<ClusteredJob> findByJobRunnerKey(JobRunnerKey var1);

    @Nonnull
    default public Collection<ClusteredJob> findByJobRunnerKeys(List<JobRunnerKey> jobRunnerKeys) {
        HashSet<ClusteredJob> clusteredJobs = new HashSet<ClusteredJob>();
        for (JobRunnerKey jobRunnerKey : jobRunnerKeys) {
            clusteredJobs.addAll(this.findByJobRunnerKey(jobRunnerKey));
        }
        return clusteredJobs;
    }

    @Nonnull
    public Map<JobId, Date> refresh();

    @Nonnull
    public Set<JobRunnerKey> findAllJobRunnerKeys();

    public boolean create(ClusteredJob var1);

    public boolean updateNextRunTime(JobId var1, @Nullable Date var2, long var3);

    public boolean delete(JobId var1);
}

