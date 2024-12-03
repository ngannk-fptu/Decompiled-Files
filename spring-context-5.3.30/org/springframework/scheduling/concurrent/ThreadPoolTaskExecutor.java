/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.core.task.TaskRejectedException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ConcurrentReferenceHashMap$ReferenceType
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureTask
 */
package org.springframework.scheduling.concurrent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

public class ThreadPoolTaskExecutor
extends ExecutorConfigurationSupport
implements AsyncListenableTaskExecutor,
SchedulingTaskExecutor {
    private final Object poolSizeMonitor = new Object();
    private int corePoolSize = 1;
    private int maxPoolSize = Integer.MAX_VALUE;
    private int keepAliveSeconds = 60;
    private int queueCapacity = Integer.MAX_VALUE;
    private boolean allowCoreThreadTimeOut = false;
    private boolean prestartAllCoreThreads = false;
    @Nullable
    private TaskDecorator taskDecorator;
    @Nullable
    private ThreadPoolExecutor threadPoolExecutor;
    private final Map<Runnable, Object> decoratedTaskMap = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCorePoolSize(int corePoolSize) {
        Object object = this.poolSizeMonitor;
        synchronized (object) {
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setCorePoolSize(corePoolSize);
            }
            this.corePoolSize = corePoolSize;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCorePoolSize() {
        Object object = this.poolSizeMonitor;
        synchronized (object) {
            return this.corePoolSize;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxPoolSize(int maxPoolSize) {
        Object object = this.poolSizeMonitor;
        synchronized (object) {
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            }
            this.maxPoolSize = maxPoolSize;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getMaxPoolSize() {
        Object object = this.poolSizeMonitor;
        synchronized (object) {
            return this.maxPoolSize;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setKeepAliveSeconds(int keepAliveSeconds) {
        Object object = this.poolSizeMonitor;
        synchronized (object) {
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
            }
            this.keepAliveSeconds = keepAliveSeconds;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getKeepAliveSeconds() {
        Object object = this.poolSizeMonitor;
        synchronized (object) {
            return this.keepAliveSeconds;
        }
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public void setPrestartAllCoreThreads(boolean prestartAllCoreThreads) {
        this.prestartAllCoreThreads = prestartAllCoreThreads;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        BlockingQueue<Runnable> queue = this.createQueue(this.queueCapacity);
        ThreadPoolExecutor executor = this.taskDecorator != null ? new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler){

            @Override
            public void execute(Runnable command) {
                Runnable decorated = ThreadPoolTaskExecutor.this.taskDecorator.decorate(command);
                if (decorated != command) {
                    ThreadPoolTaskExecutor.this.decoratedTaskMap.put(decorated, command);
                }
                super.execute(decorated);
            }
        } : new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }
        if (this.prestartAllCoreThreads) {
            executor.prestartAllCoreThreads();
        }
        this.threadPoolExecutor = executor;
        return executor;
    }

    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<Runnable>(queueCapacity);
        }
        return new SynchronousQueue<Runnable>();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        Assert.state((this.threadPoolExecutor != null ? 1 : 0) != 0, (String)"ThreadPoolTaskExecutor not initialized");
        return this.threadPoolExecutor;
    }

    public int getPoolSize() {
        if (this.threadPoolExecutor == null) {
            return this.corePoolSize;
        }
        return this.threadPoolExecutor.getPoolSize();
    }

    public int getQueueSize() {
        if (this.threadPoolExecutor == null) {
            return 0;
        }
        return this.threadPoolExecutor.getQueue().size();
    }

    public int getActiveCount() {
        if (this.threadPoolExecutor == null) {
            return 0;
        }
        return this.threadPoolExecutor.getActiveCount();
    }

    public void execute(Runnable task) {
        ThreadPoolExecutor executor = this.getThreadPoolExecutor();
        try {
            executor.execute(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, (Throwable)ex);
        }
    }

    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        this.execute(task);
    }

    public Future<?> submit(Runnable task) {
        ThreadPoolExecutor executor = this.getThreadPoolExecutor();
        try {
            return executor.submit(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, (Throwable)ex);
        }
    }

    public <T> Future<T> submit(Callable<T> task) {
        ThreadPoolExecutor executor = this.getThreadPoolExecutor();
        try {
            return executor.submit(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, (Throwable)ex);
        }
    }

    public ListenableFuture<?> submitListenable(Runnable task) {
        ThreadPoolExecutor executor = this.getThreadPoolExecutor();
        try {
            ListenableFutureTask future = new ListenableFutureTask(task, null);
            executor.execute((Runnable)future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, (Throwable)ex);
        }
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ThreadPoolExecutor executor = this.getThreadPoolExecutor();
        try {
            ListenableFutureTask future = new ListenableFutureTask(task);
            executor.execute((Runnable)future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, (Throwable)ex);
        }
    }

    @Override
    protected void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(task);
        Object original = this.decoratedTaskMap.get(task);
        if (original instanceof Future) {
            ((Future)original).cancel(true);
        }
    }
}

