/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.WillNotClose
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelTasksExecutor
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ParallelTasksExecutor.class);
    private final ThreadPoolExecutor executorService;
    private final String threadNamePrefix;
    private final Queue<TaskInfo<?>> stageTasks = new ConcurrentLinkedQueue();
    private final Queue<TaskInfo<?>> globalTasks = new ConcurrentLinkedQueue();

    ParallelTasksExecutor(@WillNotClose ThreadPoolExecutor executorService, String threadNamePrefix) {
        this.executorService = executorService;
        this.threadNamePrefix = threadNamePrefix;
    }

    public <T> Future<T> runTaskAsync(Callable<T> task, String info) {
        Future<T> future = this.executorService.submit(task);
        this.stageTasks.add(new TaskInfo<T>(future, info));
        return future;
    }

    public <T> Future<T> runGlobalTaskAsync(Callable<T> task, String info) {
        Future<T> future = this.executorService.submit(task);
        this.globalTasks.add(new TaskInfo<T>(future, info));
        return future;
    }

    private void shutdown() throws InterruptedException {
        this.executorService.shutdown();
        boolean terminated = this.executorService.awaitTermination(1L, TimeUnit.DAYS);
        if (!terminated) {
            log.warn("Executor [{}] did not terminate cleanly", (Object)this.threadNamePrefix);
        }
    }

    public void interruptAllJobs() throws InterruptedException {
        this.executorService.shutdown();
        this.stageTasks.forEach(t -> t.future.cancel(true));
        this.globalTasks.forEach(t -> t.future.cancel(true));
        this.executorService.shutdownNow();
    }

    public void waitUntilAllStageJobsComplete(@Nullable Duration jobTimeout) throws ExecutionException, InterruptedException, TimeoutException {
        log.debug("Waiting for all stage job of [{}] to complete with timeout {}.", (Object)this.threadNamePrefix, (Object)jobTimeout);
        this.waitUntilAllTasksComplete(this.stageTasks, jobTimeout, "stage");
        log.debug("All stage job of [{}] have been completed.", (Object)this.threadNamePrefix);
    }

    public int waitUntilAllGlobalJobsComplete() throws ExecutionException, InterruptedException, TimeoutException {
        log.debug("Waiting for all stage and global job of [{}] to complete without timeout.", (Object)this.threadNamePrefix);
        try {
            int n = this.waitUntilAllTasksComplete(this.stageTasks, null, "stage") + this.waitUntilAllTasksComplete(this.globalTasks, null, "global");
            return n;
        }
        finally {
            log.debug("All stage and global job of [{}] have been completed.", (Object)this.threadNamePrefix);
        }
    }

    public void waitUntilAllStageJobsComplete() throws ExecutionException, InterruptedException, TimeoutException {
        log.debug("Waiting for all job of [{}] to complete without timeout.", (Object)this.threadNamePrefix);
        this.waitUntilAllStageJobsComplete(null);
        log.debug("All job of [{}] have been completed.", (Object)this.threadNamePrefix);
    }

    int waitUntilAllTasksComplete(Queue<TaskInfo<?>> tasks, @Nullable Duration jobTimeout, String jobsType) throws InterruptedException, ExecutionException, TimeoutException {
        log.debug("Waiting for the completion of all {} jobs of [{}]", (Object)jobsType, (Object)this.threadNamePrefix);
        int taskCounter = 0;
        while (!tasks.isEmpty()) {
            TaskInfo<?> task = tasks.poll();
            log.trace("Waiting until the {} task {} finished", (Object)jobsType, (Object)task.info);
            if (jobTimeout == null) {
                task.future.get();
            } else {
                task.future.get(jobTimeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            log.trace("The {} task {} has been finished", (Object)jobsType, (Object)task.info);
            ++taskCounter;
        }
        log.debug("All {} jobs of [{}] have been finished successfully", (Object)jobsType, (Object)this.threadNamePrefix);
        return taskCounter;
    }

    @Override
    public void close() throws InterruptedException {
        this.shutdown();
    }

    private static class TaskInfo<T> {
        final Future<T> future;
        final String info;

        TaskInfo(Future<T> future, String info) {
            this.future = future;
            this.info = info;
        }
    }
}

