/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.util.concurrent.ExecutionException;

public abstract class MonitoredJobRunner
implements JobRunner {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            TaskMonitor<Void> monitor = this.start();
            monitor.get();
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success();
            return jobRunnerResponse;
        }
        catch (InterruptedException e) {
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.aborted((String)e.getMessage());
            return jobRunnerResponse;
        }
        catch (ExecutionException e) {
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.failed((Throwable)e.getCause());
            return jobRunnerResponse;
        }
        finally {
            Thread.currentThread().setContextClassLoader(ctxClassLoader);
        }
    }

    protected abstract TaskMonitor<Void> start();
}

