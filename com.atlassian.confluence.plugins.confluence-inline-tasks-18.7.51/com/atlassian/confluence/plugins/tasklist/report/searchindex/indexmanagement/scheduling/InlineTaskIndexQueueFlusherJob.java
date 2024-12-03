/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.scheduling;

import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexQueueProcessor;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.ParametersAreNonnullByDefault;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="inlineTaskIndexQueueFlusherJob")
public class InlineTaskIndexQueueFlusherJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(InlineTaskIndexQueueFlusherJob.class);
    private final IndexQueueProcessor queue;

    @Autowired
    public InlineTaskIndexQueueFlusherJob(IndexQueueProcessor queue) {
        this.queue = queue;
    }

    public @Nullable JobRunnerResponse runJob(@ParametersAreNonnullByDefault JobRunnerRequest request) {
        try {
            this.queue.flushQueue();
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            log.error("Unable to run task report index flush job", (Throwable)e);
            return JobRunnerResponse.failed((String)e.getMessage());
        }
    }
}

