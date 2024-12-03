/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  org.apache.commons.lang3.tuple.Pair
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.schedule.caesium.ConfluenceSchedulerService;
import com.atlassian.confluence.internal.schedule.persistence.dao.InternalRunDetailsDao;
import com.atlassian.confluence.schedule.managers.SchedulerRunDetailsManager;
import com.atlassian.confluence.schedule.managers.SchedulerRunDetailsPurgeMode;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.Date;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class DefaultSchedulerRunDetailsManager
implements SchedulerRunDetailsManager {
    private static final int JOBS_LIMIT_PER_PURGE = Integer.getInteger("jobs.limit.per.purge", 2000);
    private static final Logger log = LoggerFactory.getLogger(DefaultSchedulerRunDetailsManager.class);
    private final InternalRunDetailsDao internalRunDetailsDao;
    private final SchedulerService schedulerService;

    public DefaultSchedulerRunDetailsManager(InternalRunDetailsDao internalRunDetailsDao, SchedulerService schedulerService) {
        this.internalRunDetailsDao = internalRunDetailsDao;
        this.schedulerService = schedulerService;
    }

    @Override
    public Pair<Integer, Integer> purgeOldRunDetails() {
        int total = 0;
        int batches = 0;
        for (SchedulerRunDetailsPurgeMode purgeMode : SchedulerRunDetailsPurgeMode.values()) {
            boolean stop = false;
            int subtotal = 0;
            while (!stop) {
                int count = this.internalRunDetailsDao.purgeOldRunDetails(purgeMode, JOBS_LIMIT_PER_PURGE);
                ++batches;
                subtotal += count;
                stop = count == 0;
            }
            total += subtotal;
            log.info("Total number of job runs purged before {} from now: {}", (Object)purgeMode.getTimeToLiveThreshold(), (Object)subtotal);
        }
        log.info("{} job runs have been purged in {} batches", (Object)total, (Object)batches);
        return Pair.of((Object)total, (Object)batches);
    }

    @Override
    public long count(JobId jobId, long timeToLiveThreshold, RunOutcome runOutcome) {
        return this.internalRunDetailsDao.count(Optional.of(jobId), timeToLiveThreshold, runOutcome);
    }

    @Override
    public RunDetails addRunDetails(JobId jobId, Date startedAt, RunOutcome runOutcome, @Nullable String message) {
        return ((ConfluenceSchedulerService)this.schedulerService).addRunDetails(jobId, startedAt, runOutcome, message);
    }
}

