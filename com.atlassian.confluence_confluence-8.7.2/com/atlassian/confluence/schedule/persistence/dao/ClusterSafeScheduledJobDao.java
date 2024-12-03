/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.scheduler.config.JobId
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule.persistence.dao;

import com.atlassian.annotations.Internal;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.persistence.dao.CachedScheduledJobDao;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobDao;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.scheduler.config.JobId;
import com.google.common.base.Preconditions;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public class ClusterSafeScheduledJobDao
implements ScheduledJobDao {
    private final BandanaManager bandanaManager;
    private final BandanaContext configurationContext = new ConfluenceBandanaContext(CachedScheduledJobDao.CONFIGURATION_CONTEXT_KEY);

    public ClusterSafeScheduledJobDao(BandanaManager bandanaManager) {
        this.bandanaManager = (BandanaManager)Preconditions.checkNotNull((Object)bandanaManager);
    }

    @Override
    public @Nullable ScheduledJobStatus getScheduledJobStatus(JobId jobId) {
        return null;
    }

    @Override
    public void saveScheduledJobStatus(JobId jobId, ScheduledJobStatus status) {
    }

    @Override
    public void updateStatus(JobId jobId, ExecutionStatus status) {
    }

    @Override
    public void addHistory(JobId jobId, @Nullable ScheduledJobHistory history, Date nextOccurrence) {
    }

    @Override
    public void updateNextOccurrence(JobId jobId, Date nextOccurrence) {
    }

    @Override
    public void saveScheduledJobConfiguration(JobId jobId, ScheduledJobConfiguration job) {
        this.bandanaManager.setValue(this.configurationContext, String.valueOf(jobId), (Object)job);
    }

    @Override
    public @Nullable ScheduledJobConfiguration getScheduledJobConfiguration(JobId jobId) {
        return (ScheduledJobConfiguration)this.bandanaManager.getValue(this.configurationContext, String.valueOf(jobId));
    }
}

