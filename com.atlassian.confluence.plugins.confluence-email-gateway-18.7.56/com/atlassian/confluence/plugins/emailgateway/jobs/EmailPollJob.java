/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.jobs;

import com.atlassian.confluence.plugins.emailgateway.service.BulkEmailProcessingService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailPollJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(EmailPollJob.class);
    private final BulkEmailProcessingService processingService;
    private final TransactionTemplate transactionTemplate;

    public EmailPollJob(BulkEmailProcessingService processingService, TransactionTemplate transactionTemplate) {
        this.processingService = processingService;
        this.transactionTemplate = transactionTemplate;
    }

    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        log.debug("Starting Email Poll Job");
        this.transactionTemplate.execute(() -> {
            if (this.processingService.isAvailable()) {
                int processed = this.processingService.processInboundEmail();
                log.debug("Processed {} emails", (Object)processed);
            } else {
                log.debug("Processing service is not available");
            }
            return null;
        });
        return JobRunnerResponse.success();
    }
}

