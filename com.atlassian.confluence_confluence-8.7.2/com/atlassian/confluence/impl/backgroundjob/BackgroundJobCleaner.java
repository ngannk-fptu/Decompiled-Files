/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob;

import com.atlassian.confluence.impl.backgroundjob.BackgroundJobProcessor;
import com.atlassian.confluence.impl.backgroundjob.BackgroundJobResponse;
import com.atlassian.confluence.impl.backgroundjob.dao.BackgroundJobDAO;
import com.atlassian.confluence.impl.backgroundjob.domain.ArchivedBackgroundJob;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BackgroundJobCleaner
implements BackgroundJobProcessor {
    private final BackgroundJobDAO backgroundJobDAO;
    private final int COMPLETED_BACKGROUND_JOBS_TTL = Integer.getInteger("confluence.backgroundjobs.completed-jobs-ttl-in-days", 7);
    private final int COMPLETED_BACKGROUND_JOBS_BATCH_SIZE = Integer.getInteger("confluence.backgroundjobs.completed-jobs-batch-size", 50);

    public BackgroundJobCleaner(BackgroundJobDAO backgroundJobDAO) {
        this.backgroundJobDAO = backgroundJobDAO;
    }

    @Override
    public BackgroundJobResponse process(Long jobId, Map<String, Object> parameters, long recommendedTimeout) {
        Instant date = Instant.now().minus(this.COMPLETED_BACKGROUND_JOBS_TTL, ChronoUnit.DAYS);
        List<ArchivedBackgroundJob> backgroundJobsToRemove = this.backgroundJobDAO.getObsoleteArchivedJobs(date, this.COMPLETED_BACKGROUND_JOBS_BATCH_SIZE);
        backgroundJobsToRemove.forEach(this.backgroundJobDAO::removeArchivedJob);
        if (backgroundJobsToRemove.size() < this.COMPLETED_BACKGROUND_JOBS_BATCH_SIZE) {
            return BackgroundJobResponse.scheduleNextRun(ChronoUnit.DAYS, 1, Collections.emptyMap());
        }
        return BackgroundJobResponse.scheduleNextRun(ChronoUnit.MINUTES, 1, Collections.emptyMap());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

