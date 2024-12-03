/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.scheduler.config.JobId
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.persistence.dao;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobDao;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobStatusCache;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.scheduler.config.JobId;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedScheduledJobDao
implements ScheduledJobDao {
    private static final Logger log = LoggerFactory.getLogger(CachedScheduledJobDao.class);
    public static final String CONFIGURATION_CONTEXT_KEY = ScheduledJobConfiguration.class.getName();
    private final boolean SCHEDULED_JOB_HISTORY_DISABLED = Boolean.getBoolean("SCHEDULED_JOB_HISTORY_DISABLED");
    private final ScheduledJobStatusCache scheduledJobStatusCache;
    private final BandanaManager bandanaManager;
    private final BandanaContext configurationContext = new ConfluenceBandanaContext(CONFIGURATION_CONTEXT_KEY);

    public CachedScheduledJobDao(CacheFactory cacheFactory, BandanaManager bandanaManager) {
        this(ScheduledJobStatusCache.forAtlassianCache(CoreCache.SCHEDULED_JOB_STATUS.getCache(cacheFactory)), bandanaManager);
    }

    private CachedScheduledJobDao(ScheduledJobStatusCache scheduledJobStatusCache, BandanaManager bandanaManager) {
        this.scheduledJobStatusCache = scheduledJobStatusCache;
        this.bandanaManager = bandanaManager;
        if (this.SCHEDULED_JOB_HISTORY_DISABLED) {
            log.info("Job history is disabled");
        }
    }

    @Override
    public @Nullable ScheduledJobStatus getScheduledJobStatus(JobId jobId) {
        if (!this.SCHEDULED_JOB_HISTORY_DISABLED) {
            return this.scheduledJobStatusCache.get(jobId);
        }
        return null;
    }

    @Override
    public void saveScheduledJobStatus(JobId jobId, ScheduledJobStatus status) {
        if (!this.SCHEDULED_JOB_HISTORY_DISABLED) {
            this.scheduledJobStatusCache.put(jobId, status);
        }
    }

    @Override
    public void addHistory(JobId jobId, @Nullable ScheduledJobHistory history, Date nextOccurrence) {
        ScheduledJobStatus jobStatus;
        if (!this.SCHEDULED_JOB_HISTORY_DISABLED && (jobStatus = this.getScheduledJobStatus(jobId)) != null) {
            jobStatus.setNextExecution(nextOccurrence);
            if (history != null) {
                jobStatus.addHistory(history);
            }
            this.scheduledJobStatusCache.put(jobId, jobStatus);
        }
    }

    @Override
    public void updateStatus(JobId jobId, ExecutionStatus status) {
        if (!this.SCHEDULED_JOB_HISTORY_DISABLED) {
            ScheduledJobStatus jobStatus = this.getScheduledJobStatus(jobId);
            if (jobStatus == null) {
                return;
            }
            jobStatus.setStatus(status);
            this.scheduledJobStatusCache.put(jobId, jobStatus);
        }
    }

    @Override
    public void updateNextOccurrence(JobId jobId, Date nextOccurrence) {
        if (!this.SCHEDULED_JOB_HISTORY_DISABLED) {
            ScheduledJobStatus jobStatus = this.getScheduledJobStatus(jobId);
            if (jobStatus == null) {
                return;
            }
            jobStatus.setNextExecution(nextOccurrence);
            this.scheduledJobStatusCache.put(jobId, jobStatus);
        }
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

