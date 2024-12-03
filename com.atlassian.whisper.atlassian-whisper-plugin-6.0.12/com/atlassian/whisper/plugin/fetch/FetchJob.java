/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.whisper.plugin.api.MessageFetchService
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.fetch;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.whisper.plugin.api.MessageFetchService;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchJob
implements JobRunner {
    private static final Logger LOG = LoggerFactory.getLogger(FetchJob.class);
    private final MessageFetchService fetchService;

    public FetchJob(MessageFetchService fetchService) {
        this.fetchService = fetchService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRequest) {
        try {
            LOG.debug("Run fetch service.");
            this.fetchService.fetch();
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success();
            return jobRunnerResponse;
        }
        catch (Exception e) {
            LOG.debug("Run fetch failed.", (Throwable)e);
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.failed((Throwable)e);
            return jobRunnerResponse;
        }
        finally {
            LOG.debug("Done fetch service.");
        }
    }
}

