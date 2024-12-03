/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  commonj.work.Work
 *  commonj.work.WorkException
 *  commonj.work.WorkItem
 *  commonj.work.WorkListener
 *  commonj.work.WorkManager
 *  commonj.work.WorkRejectedException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.core.task.TaskDecorator
 *  org.springframework.core.task.TaskRejectedException
 *  org.springframework.jndi.JndiLocatorSupport
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.SchedulingException
 *  org.springframework.scheduling.SchedulingTaskExecutor
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureTask
 */
package org.springframework.scheduling.commonj;

import commonj.work.Work;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;
import commonj.work.WorkRejectedException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.naming.NamingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.commonj.DelegatingWork;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

@Deprecated
public class WorkManagerTaskExecutor
extends JndiLocatorSupport
implements AsyncListenableTaskExecutor,
SchedulingTaskExecutor,
WorkManager,
InitializingBean {
    @Nullable
    private WorkManager workManager;
    @Nullable
    private String workManagerName;
    @Nullable
    private WorkListener workListener;
    @Nullable
    private TaskDecorator taskDecorator;

    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

    public void setWorkManagerName(String workManagerName) {
        this.workManagerName = workManagerName;
    }

    public void setWorkListener(WorkListener workListener) {
        this.workListener = workListener;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    public void afterPropertiesSet() throws NamingException {
        if (this.workManager == null) {
            if (this.workManagerName == null) {
                throw new IllegalArgumentException("Either 'workManager' or 'workManagerName' must be specified");
            }
            this.workManager = (WorkManager)this.lookup(this.workManagerName, WorkManager.class);
        }
    }

    private WorkManager obtainWorkManager() {
        Assert.state((this.workManager != null ? 1 : 0) != 0, (String)"No WorkManager specified");
        return this.workManager;
    }

    public void execute(Runnable task) {
        DelegatingWork work = new DelegatingWork(this.taskDecorator != null ? this.taskDecorator.decorate(task) : task);
        try {
            if (this.workListener != null) {
                this.obtainWorkManager().schedule((Work)work, this.workListener);
            } else {
                this.obtainWorkManager().schedule((Work)work);
            }
        }
        catch (WorkRejectedException ex) {
            throw new TaskRejectedException("CommonJ WorkManager did not accept task: " + task, (Throwable)ex);
        }
        catch (WorkException ex) {
            throw new SchedulingException("Could not schedule task on CommonJ WorkManager", (Throwable)ex);
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

    public WorkItem schedule(Work work) throws WorkException, IllegalArgumentException {
        return this.obtainWorkManager().schedule(work);
    }

    public WorkItem schedule(Work work, WorkListener workListener) throws WorkException {
        return this.obtainWorkManager().schedule(work, workListener);
    }

    public boolean waitForAll(Collection workItems, long timeout) throws InterruptedException {
        return this.obtainWorkManager().waitForAll(workItems, timeout);
    }

    public Collection waitForAny(Collection workItems, long timeout) throws InterruptedException {
        return this.obtainWorkManager().waitForAny(workItems, timeout);
    }
}

