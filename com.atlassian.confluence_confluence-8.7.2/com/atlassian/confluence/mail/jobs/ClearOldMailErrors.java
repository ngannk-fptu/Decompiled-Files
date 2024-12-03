/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.Task
 *  com.atlassian.core.task.TaskQueue
 *  com.atlassian.core.task.TaskQueueWithErrorQueue
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.jobs;

import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;
import com.atlassian.core.task.TaskQueueWithErrorQueue;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearOldMailErrors
implements JobRunner {
    private static final long THRESHOLD_MILLIS = TimeUnit.DAYS.toMillis(2L);
    private static final Logger log = LoggerFactory.getLogger(ClearOldMailErrors.class);
    private final TaskQueueWithErrorQueue mailTaskQueue;

    public ClearOldMailErrors(TaskQueueWithErrorQueue mailTaskQueue) {
        this.mailTaskQueue = mailTaskQueue;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        TaskQueue errorQueue = this.mailTaskQueue.getErrorQueue();
        List<MailQueueItem> tasksToKeep = errorQueue.getTasks().stream().filter(task -> task instanceof MailQueueItem).map(task -> (MailQueueItem)task).filter(task -> {
            if (System.currentTimeMillis() - task.getDateQueued().getTime() < THRESHOLD_MILLIS) {
                return true;
            }
            log.info("Removing item from error queue:" + task.getSubject());
            return false;
        }).collect(Collectors.toList());
        errorQueue.clear();
        tasksToKeep.forEach(task -> errorQueue.addTask((Task)task));
        return null;
    }
}

