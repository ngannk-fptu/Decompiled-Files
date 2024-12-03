/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  io.atlassian.util.concurrent.ThreadFactories
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ParallelTasksExecutorFactory {
    public ParallelTasksExecutor create(int threadsNumber, String threadNamePrefix) {
        ThreadPoolExecutor executorService = this.createExecutor(threadsNumber, threadNamePrefix);
        executorService.prestartAllCoreThreads();
        return new ParallelTasksExecutor(executorService, threadNamePrefix);
    }

    public ParallelTasksExecutor create(BackupRestoreJob job, int threadsNumber) {
        return this.create(threadsNumber, this.getThreadNamePrefix(job.getJobScope(), job.getJobOperation()));
    }

    private ThreadPoolExecutor createExecutor(int threadsNumber, String threadNamePrefix) {
        return new ThreadPoolExecutor(threadsNumber, threadsNumber, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(threadsNumber), ThreadFactories.namedThreadFactory((String)threadNamePrefix), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private String getThreadNamePrefix(JobScope jobScope, JobOperation jobOperation) {
        return String.format("backuprestore-%s-%s", jobScope, jobOperation).toLowerCase();
    }
}

