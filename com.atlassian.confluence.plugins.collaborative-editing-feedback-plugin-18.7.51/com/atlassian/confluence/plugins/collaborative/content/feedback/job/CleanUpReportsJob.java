/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.job;

import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ReconciliationHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.SynchronyRequestsHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.FileNameUtils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cleanUpReportsJob")
public class CleanUpReportsJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(CleanUpReportsJob.class);
    private final SettingsManager settingsManager;
    private final ReconciliationHistoryDao reconciliationHistoryDao;
    private final SynchronyRequestsHistoryDao synchronyRequestsHistoryDao;

    @Autowired
    public CleanUpReportsJob(SettingsManager settingsManager, ReconciliationHistoryDao reconciliationHistoryDao, SynchronyRequestsHistoryDao synchronyRequestsHistoryDao) {
        this.settingsManager = settingsManager;
        this.reconciliationHistoryDao = reconciliationHistoryDao;
        this.synchronyRequestsHistoryDao = synchronyRequestsHistoryDao;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            if (this.settingsManager.dataRetention() > 0) {
                long currentTimeStamp = new Date().getTime();
                this.deleteFiles(currentTimeStamp);
                this.reconciliationHistoryDao.cleanUp(Date.from(Instant.now().minus(this.settingsManager.dataRetention(), ChronoUnit.HOURS)));
                this.synchronyRequestsHistoryDao.cleanUp(Date.from(Instant.now().minus(this.settingsManager.dataRetention(), ChronoUnit.HOURS)));
            }
        }
        catch (Exception e) {
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    private void deleteFiles(long currentTimeStamp) {
        try {
            Arrays.stream(Objects.requireNonNull(this.settingsManager.getDestinationFolder().listFiles())).filter(file -> FileNameUtils.isValidFileName(file.getName())).filter(file -> CleanUpReportsJob.toHours(currentTimeStamp - FileNameUtils.getCreatedTimestamp(file.getName())) > (double)this.settingsManager.dataRetention()).forEach(file -> {
                boolean result = file.delete();
                log.info("Deleting file {} with result {}", (Object)file.getName(), (Object)result);
            });
        }
        catch (Exception e) {
            log.debug("Error deleting report files", (Throwable)e);
            log.error("Error deleting report files: {}", (Object)e.toString());
        }
    }

    private static double toHours(long millis) {
        return (double)millis / 3600000.0;
    }
}

