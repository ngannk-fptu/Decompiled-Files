/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.SchedulerConfigException
 *  org.quartz.simpl.SimpleThreadPool
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.scheduling.SchedulingException
 *  org.springframework.scheduling.SchedulingTaskExecutor
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureTask
 */
package org.springframework.scheduling.quartz;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.quartz.SchedulerConfigException;
import org.quartz.simpl.SimpleThreadPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

public class SimpleThreadPoolTaskExecutor
extends SimpleThreadPool
implements AsyncListenableTaskExecutor,
SchedulingTaskExecutor,
InitializingBean,
DisposableBean {
    private boolean waitForJobsToCompleteOnShutdown = false;

    public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    public void afterPropertiesSet() throws SchedulerConfigException {
        this.initialize();
    }

    public void execute(Runnable task) {
        Assert.notNull((Object)task, (String)"Runnable must not be null");
        if (!this.runInThread(task)) {
            throw new SchedulingException("Quartz SimpleThreadPool already shut down");
        }
    }

    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        this.execute(task);
    }

    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<Object>(task, null);
        this.execute(future);
        return future;
    }

    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<T>(task);
        this.execute(future);
        return future;
    }

    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask future = new ListenableFutureTask(task, null);
        this.execute((Runnable)future);
        return future;
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask future = new ListenableFutureTask(task);
        this.execute((Runnable)future);
        return future;
    }

    public void destroy() {
        this.shutdown(this.waitForJobsToCompleteOnShutdown);
    }
}

