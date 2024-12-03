/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.BootstrapContext
 *  javax.resource.spi.work.ExecutionContext
 *  javax.resource.spi.work.Work
 *  javax.resource.spi.work.WorkException
 *  javax.resource.spi.work.WorkListener
 *  javax.resource.spi.work.WorkManager
 *  javax.resource.spi.work.WorkRejectedException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.core.task.TaskRejectedException
 *  org.springframework.core.task.TaskTimeoutException
 *  org.springframework.jndi.JndiLocatorSupport
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.SchedulingException
 *  org.springframework.scheduling.SchedulingTaskExecutor
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureTask
 */
package org.springframework.jca.work;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.naming.NamingException;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.resource.spi.work.WorkRejectedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.core.task.TaskTimeoutException;
import org.springframework.jca.context.BootstrapContextAware;
import org.springframework.jca.work.DelegatingWork;
import org.springframework.jca.work.SimpleTaskWorkManager;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

public class WorkManagerTaskExecutor
extends JndiLocatorSupport
implements AsyncListenableTaskExecutor,
SchedulingTaskExecutor,
WorkManager,
BootstrapContextAware,
InitializingBean {
    @Nullable
    private WorkManager workManager;
    @Nullable
    private String workManagerName;
    private boolean blockUntilStarted = false;
    private boolean blockUntilCompleted = false;
    @Nullable
    private WorkListener workListener;
    @Nullable
    private TaskDecorator taskDecorator;

    public WorkManagerTaskExecutor() {
    }

    public WorkManagerTaskExecutor(WorkManager workManager) {
        this.setWorkManager(workManager);
    }

    public void setWorkManager(WorkManager workManager) {
        Assert.notNull((Object)workManager, (String)"WorkManager must not be null");
        this.workManager = workManager;
    }

    public void setWorkManagerName(String workManagerName) {
        this.workManagerName = workManagerName;
    }

    @Override
    public void setBootstrapContext(BootstrapContext bootstrapContext) {
        Assert.notNull((Object)bootstrapContext, (String)"BootstrapContext must not be null");
        this.workManager = bootstrapContext.getWorkManager();
    }

    public void setBlockUntilStarted(boolean blockUntilStarted) {
        this.blockUntilStarted = blockUntilStarted;
    }

    public void setBlockUntilCompleted(boolean blockUntilCompleted) {
        this.blockUntilCompleted = blockUntilCompleted;
    }

    public void setWorkListener(@Nullable WorkListener workListener) {
        this.workListener = workListener;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    public void afterPropertiesSet() throws NamingException {
        if (this.workManager == null) {
            this.workManager = this.workManagerName != null ? (WorkManager)this.lookup(this.workManagerName, WorkManager.class) : this.getDefaultWorkManager();
        }
    }

    protected WorkManager getDefaultWorkManager() {
        return new SimpleTaskWorkManager();
    }

    private WorkManager obtainWorkManager() {
        Assert.state((this.workManager != null ? 1 : 0) != 0, (String)"No WorkManager specified");
        return this.workManager;
    }

    public void execute(Runnable task) {
        this.execute(task, Long.MAX_VALUE);
    }

    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        DelegatingWork work = new DelegatingWork(this.taskDecorator != null ? this.taskDecorator.decorate(task) : task);
        try {
            if (this.blockUntilCompleted) {
                if (startTimeout != Long.MAX_VALUE || this.workListener != null) {
                    this.obtainWorkManager().doWork((Work)work, startTimeout, null, this.workListener);
                } else {
                    this.obtainWorkManager().doWork((Work)work);
                }
            } else if (this.blockUntilStarted) {
                if (startTimeout != Long.MAX_VALUE || this.workListener != null) {
                    this.obtainWorkManager().startWork((Work)work, startTimeout, null, this.workListener);
                } else {
                    this.obtainWorkManager().startWork((Work)work);
                }
            } else if (startTimeout != Long.MAX_VALUE || this.workListener != null) {
                this.obtainWorkManager().scheduleWork((Work)work, startTimeout, null, this.workListener);
            } else {
                this.obtainWorkManager().scheduleWork((Work)work);
            }
        }
        catch (WorkRejectedException ex) {
            if ("1".equals(ex.getErrorCode())) {
                throw new TaskTimeoutException("JCA WorkManager rejected task because of timeout: " + task, (Throwable)ex);
            }
            throw new TaskRejectedException("JCA WorkManager rejected task: " + task, (Throwable)ex);
        }
        catch (WorkException ex) {
            throw new SchedulingException("Could not schedule task on JCA WorkManager", (Throwable)ex);
        }
    }

    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<Object>(task, null);
        this.execute(future, Long.MAX_VALUE);
        return future;
    }

    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<T>(task);
        this.execute(future, Long.MAX_VALUE);
        return future;
    }

    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask future = new ListenableFutureTask(task, null);
        this.execute((Runnable)future, Long.MAX_VALUE);
        return future;
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask future = new ListenableFutureTask(task);
        this.execute((Runnable)future, Long.MAX_VALUE);
        return future;
    }

    public void doWork(Work work) throws WorkException {
        this.obtainWorkManager().doWork(work);
    }

    public void doWork(Work work, long delay, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
        this.obtainWorkManager().doWork(work, delay, executionContext, workListener);
    }

    public long startWork(Work work) throws WorkException {
        return this.obtainWorkManager().startWork(work);
    }

    public long startWork(Work work, long delay, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
        return this.obtainWorkManager().startWork(work, delay, executionContext, workListener);
    }

    public void scheduleWork(Work work) throws WorkException {
        this.obtainWorkManager().scheduleWork(work);
    }

    public void scheduleWork(Work work, long delay, ExecutionContext executionContext, WorkListener workListener) throws WorkException {
        this.obtainWorkManager().scheduleWork(work, delay, executionContext, workListener);
    }
}

