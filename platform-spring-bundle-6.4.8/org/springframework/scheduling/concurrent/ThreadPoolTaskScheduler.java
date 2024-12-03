/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.concurrent;

import java.time.Clock;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ReschedulingRunnable;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ErrorHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

public class ThreadPoolTaskScheduler
extends ExecutorConfigurationSupport
implements AsyncListenableTaskExecutor,
SchedulingTaskExecutor,
TaskScheduler {
    private volatile int poolSize = 1;
    private volatile boolean removeOnCancelPolicy;
    private volatile boolean continueExistingPeriodicTasksAfterShutdownPolicy;
    private volatile boolean executeExistingDelayedTasksAfterShutdownPolicy = true;
    @Nullable
    private volatile ErrorHandler errorHandler;
    private Clock clock = Clock.systemDefaultZone();
    @Nullable
    private ScheduledExecutorService scheduledExecutor;
    private final Map<Object, ListenableFuture<?>> listenableFutureMap = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public void setPoolSize(int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)this.scheduledExecutor).setCorePoolSize(poolSize);
        }
        this.poolSize = poolSize;
    }

    public void setRemoveOnCancelPolicy(boolean flag) {
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)this.scheduledExecutor).setRemoveOnCancelPolicy(flag);
        }
        this.removeOnCancelPolicy = flag;
    }

    public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean flag) {
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)this.scheduledExecutor).setContinueExistingPeriodicTasksAfterShutdownPolicy(flag);
        }
        this.continueExistingPeriodicTasksAfterShutdownPolicy = flag;
    }

    public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean flag) {
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)this.scheduledExecutor).setExecuteExistingDelayedTasksAfterShutdownPolicy(flag);
        }
        this.executeExistingDelayedTasksAfterShutdownPolicy = flag;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Clock getClock() {
        return this.clock;
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        this.scheduledExecutor = this.createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor scheduledPoolExecutor = (ScheduledThreadPoolExecutor)this.scheduledExecutor;
            if (this.removeOnCancelPolicy) {
                scheduledPoolExecutor.setRemoveOnCancelPolicy(true);
            }
            if (this.continueExistingPeriodicTasksAfterShutdownPolicy) {
                scheduledPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
            }
            if (!this.executeExistingDelayedTasksAfterShutdownPolicy) {
                scheduledPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            }
        }
        return this.scheduledExecutor;
    }

    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }

    public ScheduledExecutorService getScheduledExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor != null, "ThreadPoolTaskScheduler not initialized");
        return this.scheduledExecutor;
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor instanceof ScheduledThreadPoolExecutor, "No ScheduledThreadPoolExecutor available");
        return (ScheduledThreadPoolExecutor)this.scheduledExecutor;
    }

    public int getPoolSize() {
        if (this.scheduledExecutor == null) {
            return this.poolSize;
        }
        return this.getScheduledThreadPoolExecutor().getPoolSize();
    }

    public int getActiveCount() {
        if (this.scheduledExecutor == null) {
            return 0;
        }
        return this.getScheduledThreadPoolExecutor().getActiveCount();
    }

    @Deprecated
    public boolean isRemoveOnCancelPolicy() {
        if (this.scheduledExecutor == null) {
            return this.removeOnCancelPolicy;
        }
        return this.getScheduledThreadPoolExecutor().getRemoveOnCancelPolicy();
    }

    @Override
    public void execute(Runnable task) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            executor.execute(this.errorHandlingTask(task, false));
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        this.execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            return executor.submit(this.errorHandlingTask(task, false));
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            Callable<T> taskToUse = task;
            ErrorHandler errorHandler = this.errorHandler;
            if (errorHandler != null) {
                taskToUse = new DelegatingErrorHandlingCallable<T>(task, errorHandler);
            }
            return executor.submit(taskToUse);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            ListenableFutureTask<Object> listenableFuture = new ListenableFutureTask<Object>(task, null);
            this.executeAndTrack(executor, listenableFuture);
            return listenableFuture;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            ListenableFutureTask<T> listenableFuture = new ListenableFutureTask<T>(task);
            this.executeAndTrack(executor, listenableFuture);
            return listenableFuture;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    private void executeAndTrack(ExecutorService executor, ListenableFutureTask<?> listenableFuture) {
        Future<?> scheduledFuture = executor.submit(this.errorHandlingTask(listenableFuture, false));
        this.listenableFutureMap.put(scheduledFuture, listenableFuture);
        listenableFuture.addCallback(result -> this.listenableFutureMap.remove(scheduledFuture), ex -> this.listenableFutureMap.remove(scheduledFuture));
    }

    @Override
    protected void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(task);
        ListenableFuture<?> listenableFuture = this.listenableFutureMap.get(task);
        if (listenableFuture != null) {
            listenableFuture.cancel(true);
        }
    }

    @Override
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            ErrorHandler errorHandler = this.errorHandler;
            if (errorHandler == null) {
                errorHandler = TaskUtils.getDefaultErrorHandler(true);
            }
            return new ReschedulingRunnable(task, trigger, this.clock, executor, errorHandler).schedule();
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        long delay = startTime.getTime() - this.clock.millis();
        try {
            return executor.schedule(this.errorHandlingTask(task, false), delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        long initialDelay = startTime.getTime() - this.clock.millis();
        try {
            return executor.scheduleAtFixedRate(this.errorHandlingTask(task, true), initialDelay, period, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            return executor.scheduleAtFixedRate(this.errorHandlingTask(task, true), 0L, period, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        long initialDelay = startTime.getTime() - this.clock.millis();
        try {
            return executor.scheduleWithFixedDelay(this.errorHandlingTask(task, true), initialDelay, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            return executor.scheduleWithFixedDelay(this.errorHandlingTask(task, true), 0L, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    private Runnable errorHandlingTask(Runnable task, boolean isRepeatingTask) {
        return TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
    }

    private static class DelegatingErrorHandlingCallable<V>
    implements Callable<V> {
        private final Callable<V> delegate;
        private final ErrorHandler errorHandler;

        public DelegatingErrorHandlingCallable(Callable<V> delegate, ErrorHandler errorHandler) {
            this.delegate = delegate;
            this.errorHandler = errorHandler;
        }

        @Override
        @Nullable
        public V call() throws Exception {
            try {
                return this.delegate.call();
            }
            catch (Throwable ex) {
                this.errorHandler.handleError(ex);
                return null;
            }
        }
    }
}

