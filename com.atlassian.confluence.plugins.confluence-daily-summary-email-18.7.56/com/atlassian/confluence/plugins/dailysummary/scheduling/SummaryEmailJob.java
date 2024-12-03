/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dailysummary.scheduling;

import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SummaryEmailJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(SummaryEmailJob.class);
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final SummaryEmailService summaryEmailService;

    public SummaryEmailJob(SummaryEmailService summaryEmailService) {
        this.summaryEmailService = summaryEmailService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        log.info("Executing scheduled Summary Email Job");
        if (!this.lock.compareAndSet(false, true)) {
            log.warn("MisFired for the Scheduled Summary Email Job");
            return JobRunnerResponse.aborted((String)"Job already running");
        }
        try {
            Date fireTime = request.getStartTime();
            int numSent = this.summaryEmailService.sendEmailForDate(fireTime);
            log.info("Completed scheduled Summary email job, {} emails added to notification queue", (Object)numSent);
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success();
            return jobRunnerResponse;
        }
        finally {
            this.lock.set(false);
        }
    }
}

