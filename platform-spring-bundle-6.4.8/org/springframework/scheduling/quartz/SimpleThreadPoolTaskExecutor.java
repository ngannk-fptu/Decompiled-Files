/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.SchedulerConfigException
 *  org.quartz.simpl.SimpleThreadPool
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

    @Override
    public void afterPropertiesSet() throws SchedulerConfigException {
        this.initialize();
    }

    @Override
    public void execute(Runnable task) {
        Assert.notNull((Object)task, "Runnable must not be null");
        if (!this.runInThread(task)) {
            throw new SchedulingException("Quartz SimpleThreadPool already shut down");
        }
    }

    @Override
    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        this.execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<Object>(task, null);
        this.execute(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<T>(task);
        this.execute(future);
        return future;
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
        this.execute(future);
        return future;
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
        this.execute(future);
        return future;
    }

    @Override
    public void destroy() {
        this.shutdown(this.waitForJobsToCompleteOnShutdown);
    }
}

