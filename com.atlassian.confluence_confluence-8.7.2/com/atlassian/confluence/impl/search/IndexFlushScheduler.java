/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexFlushScheduler {
    private static final Duration BATCH_WAIT_TIME = Duration.ofSeconds(Long.getLong("confluence.IndexFlushScheduler.batchWaitTimeSeconds", 5L));
    private static final Duration MAX_BACKOFF_TIME = Duration.ofMinutes(Long.getLong("confluence.IndexFlushScheduler.maxBackOffTimeMinutes", 10L));
    private static final Logger log = LoggerFactory.getLogger(IndexFlushScheduler.class);
    private final SchedulerService schedulerService;
    private final String jobRunnerKeyAndId;
    private final RunMode runMode;
    private final Duration journalBlindTime;
    private final BooleanSupplier indexFlushEnabledSupplier;
    private final ResettableLazyReference<Instant> firstRunTime;
    private Duration firstRunDelay = BATCH_WAIT_TIME;
    private volatile boolean flushRequested = false;

    public IndexFlushScheduler(SchedulerService schedulerService, String jobRunnerKeyAndId, RunMode runMode, long journalIgnoreWithinMillis, IncrementalIndexManager indexManager, FullReindexManager fullReindexManager) {
        this(schedulerService, jobRunnerKeyAndId, runMode, journalIgnoreWithinMillis, indexManager, fullReindexManager, () -> true);
    }

    public IndexFlushScheduler(SchedulerService schedulerService, String jobRunnerKeyAndId, RunMode runMode, long journalIgnoreWithinMillis, IncrementalIndexManager indexManager, FullReindexManager fullReindexManager, BooleanSupplier indexFlushEnabledSupplier) {
        this.schedulerService = schedulerService;
        this.jobRunnerKeyAndId = jobRunnerKeyAndId;
        this.runMode = runMode;
        this.indexFlushEnabledSupplier = indexFlushEnabledSupplier;
        this.journalBlindTime = Duration.ofMillis(journalIgnoreWithinMillis + 100L);
        this.firstRunTime = new ResettableLazyReference<Instant>(){

            protected Instant create() {
                return IndexFlushScheduler.this.setIntervalJob();
            }
        };
        schedulerService.registerJobRunner(JobRunnerKey.of((String)jobRunnerKeyAndId), request -> {
            try {
                if (indexManager.isFlushing() || fullReindexManager.isReIndexing()) {
                    return JobRunnerResponse.aborted((String)"Flushing or indexing is already in progress");
                }
                this.flushRequested = false;
                indexManager.flushQueue(IndexManager.IndexQueueFlushMode.ONLY_FIRST_BATCH);
                this.firstRunDelay = BATCH_WAIT_TIME;
                if (this.flushRequested) {
                    return JobRunnerResponse.success((String)"Flush requested during flush, will flush more");
                }
                int queueSize = indexManager.getQueueSize();
                if (queueSize == 0) {
                    ResettableLazyReference<Instant> resettableLazyReference = this.firstRunTime;
                    synchronized (resettableLazyReference) {
                        if (this.flushRequested) {
                            return JobRunnerResponse.success((String)"Flush requested during flush, will flush more");
                        }
                        this.unsetIntervalJob();
                        return JobRunnerResponse.success((String)"Flush done");
                    }
                }
                return JobRunnerResponse.success((String)("Will flush more, queue size: " + queueSize));
            }
            catch (RuntimeException e) {
                Duration twoTimes = this.firstRunDelay.multipliedBy(2L);
                this.firstRunDelay = twoTimes.compareTo(MAX_BACKOFF_TIME) < 0 ? twoTimes : MAX_BACKOFF_TIME;
                this.setIntervalJob();
                log.warn("Failed to flush index queue {}, retry in {}s", new Object[]{jobRunnerKeyAndId, this.firstRunDelay.getSeconds(), e});
                return JobRunnerResponse.failed((Throwable)e);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void requestFlush() {
        if (this.indexFlushEnabledSupplier.getAsBoolean()) {
            ResettableLazyReference<Instant> resettableLazyReference = this.firstRunTime;
            synchronized (resettableLazyReference) {
                this.flushRequested = true;
                this.getFirstRunTime();
            }
        }
    }

    @VisibleForTesting
    public Instant getFirstRunTime() {
        return (Instant)this.firstRunTime.get();
    }

    private Instant setIntervalJob() {
        try {
            Duration max = this.journalBlindTime.compareTo(this.firstRunDelay) < 0 ? this.firstRunDelay : this.journalBlindTime;
            Instant firstRunTime = Instant.now().plus(max);
            Schedule schedule = Schedule.forInterval((long)BATCH_WAIT_TIME.toMillis(), (Date)Date.from(firstRunTime));
            JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)this.jobRunnerKeyAndId)).withRunMode(this.runMode).withSchedule(schedule);
            this.schedulerService.scheduleJob(JobId.of((String)this.jobRunnerKeyAndId), jobConfig);
            log.debug("Scheduled to flush index queue {} at {}", (Object)this.jobRunnerKeyAndId, (Object)firstRunTime);
            return firstRunTime;
        }
        catch (SchedulerServiceException e) {
            log.warn("Failed to schedule flush for index queue {}", (Object)this.jobRunnerKeyAndId, (Object)e);
            this.firstRunTime.reset();
            return Instant.MIN;
        }
    }

    private void unsetIntervalJob() {
        this.schedulerService.unscheduleJob(JobId.of((String)this.jobRunnerKeyAndId));
        this.firstRunTime.reset();
    }
}

