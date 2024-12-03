/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.jobs;

import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadAdminService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanExpiredStagedEmailsJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(CleanExpiredStagedEmailsJob.class);
    private static final int EXPIRY_DAYS = 1;
    private final StagedEmailThreadAdminService service;

    public CleanExpiredStagedEmailsJob(StagedEmailThreadAdminService service) {
        this.service = service;
    }

    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        log.debug("Starting Email-to-Page Cleanup Job");
        DateTime expiry = new DateTime().minusDays(1);
        int removed = this.service.clearExpiredEmailThreads(expiry);
        if (removed > 0) {
            log.info("Removed {} expired emails", (Object)removed);
        } else {
            log.debug("No expired emails to remove");
        }
        return JobRunnerResponse.success();
    }
}

