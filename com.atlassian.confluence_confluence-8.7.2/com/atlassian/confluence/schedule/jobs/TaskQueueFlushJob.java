/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.TaskQueue
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.jobs;

import com.atlassian.core.task.TaskQueue;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskQueueFlushJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(TaskQueueFlushJob.class);
    private final TaskQueue queue;

    public TaskQueueFlushJob(TaskQueue queue) {
        this.queue = queue;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        if (this.queue.isFlushing()) {
            log.debug("Task queue {} was already being flushed - skipped this time", (Object)this.queue);
            return JobRunnerResponse.aborted((String)("Task queue " + this.queue + " was already being flushed"));
        }
        log.debug("Executing tasks for queue {}", (Object)this.queue);
        this.queue.flush();
        return JobRunnerResponse.success((String)("Executed tasks for queue " + this.queue));
    }
}

