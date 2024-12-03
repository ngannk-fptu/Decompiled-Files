/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.work.ExecutionContext
 *  javax.resource.spi.work.Work
 *  javax.resource.spi.work.WorkAdapter
 *  javax.resource.spi.work.WorkCompletedException
 *  javax.resource.spi.work.WorkEvent
 *  javax.resource.spi.work.WorkException
 *  javax.resource.spi.work.WorkListener
 *  javax.resource.spi.work.WorkManager
 *  javax.resource.spi.work.WorkRejectedException
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.SyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.core.task.TaskRejectedException
 *  org.springframework.core.task.TaskTimeoutException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.work;

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkAdapter;
import javax.resource.spi.work.WorkCompletedException;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.resource.spi.work.WorkRejectedException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.core.task.TaskTimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SimpleTaskWorkManager
implements WorkManager {
    @Nullable
    private TaskExecutor syncTaskExecutor = new SyncTaskExecutor();
    @Nullable
    private AsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();

    public void setSyncTaskExecutor(TaskExecutor syncTaskExecutor) {
        this.syncTaskExecutor = syncTaskExecutor;
    }

    public void setAsyncTaskExecutor(AsyncTaskExecutor asyncTaskExecutor) {
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    public void doWork(Work work) throws WorkException {
        this.doWork(work, Long.MAX_VALUE, null, null);
    }

    public void doWork(Work work, long startTimeout, @Nullable ExecutionContext executionContext, @Nullable WorkListener workListener) throws WorkException {
        Assert.state((this.syncTaskExecutor != null ? 1 : 0) != 0, (String)"No 'syncTaskExecutor' set");
        this.executeWork(this.syncTaskExecutor, work, startTimeout, false, executionContext, workListener);
    }

    public long startWork(Work work) throws WorkException {
        return this.startWork(work, Long.MAX_VALUE, null, null);
    }

    public long startWork(Work work, long startTimeout, @Nullable ExecutionContext executionContext, @Nullable WorkListener workListener) throws WorkException {
        Assert.state((this.asyncTaskExecutor != null ? 1 : 0) != 0, (String)"No 'asyncTaskExecutor' set");
        return this.executeWork((TaskExecutor)this.asyncTaskExecutor, work, startTimeout, true, executionContext, workListener);
    }

    public void scheduleWork(Work work) throws WorkException {
        this.scheduleWork(work, Long.MAX_VALUE, null, null);
    }

    public void scheduleWork(Work work, long startTimeout, @Nullable ExecutionContext executionContext, @Nullable WorkListener workListener) throws WorkException {
        Assert.state((this.asyncTaskExecutor != null ? 1 : 0) != 0, (String)"No 'asyncTaskExecutor' set");
        this.executeWork((TaskExecutor)this.asyncTaskExecutor, work, startTimeout, false, executionContext, workListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long executeWork(TaskExecutor taskExecutor, Work work, long startTimeout, boolean blockUntilStarted, @Nullable ExecutionContext executionContext, @Nullable WorkListener workListener) throws WorkException {
        boolean isAsync;
        if (executionContext != null && executionContext.getXid() != null) {
            throw new WorkException("SimpleTaskWorkManager does not supported imported XIDs: " + executionContext.getXid());
        }
        WorkListener workListenerToUse = workListener;
        if (workListenerToUse == null) {
            workListenerToUse = new WorkAdapter();
        }
        DelegatingWorkAdapter workHandle = new DelegatingWorkAdapter(work, workListenerToUse, !(isAsync = taskExecutor instanceof AsyncTaskExecutor));
        try {
            if (isAsync) {
                ((AsyncTaskExecutor)taskExecutor).execute((Runnable)((Object)workHandle), startTimeout);
            } else {
                taskExecutor.execute((Runnable)((Object)workHandle));
            }
        }
        catch (TaskTimeoutException ex) {
            WorkRejectedException wex = new WorkRejectedException("TaskExecutor rejected Work because of timeout: " + work, (Throwable)ex);
            wex.setErrorCode("1");
            workListenerToUse.workRejected(new WorkEvent((Object)this, 2, work, (WorkException)((Object)wex)));
            throw wex;
        }
        catch (TaskRejectedException ex) {
            WorkRejectedException wex = new WorkRejectedException("TaskExecutor rejected Work: " + work, (Throwable)ex);
            wex.setErrorCode("-1");
            workListenerToUse.workRejected(new WorkEvent((Object)this, 2, work, (WorkException)((Object)wex)));
            throw wex;
        }
        catch (Throwable ex) {
            WorkException wex = new WorkException("TaskExecutor failed to execute Work: " + work, ex);
            wex.setErrorCode("-1");
            throw wex;
        }
        if (isAsync) {
            workListenerToUse.workAccepted(new WorkEvent((Object)this, 1, work, null));
        }
        if (blockUntilStarted) {
            long acceptanceTime = System.currentTimeMillis();
            Object object = workHandle.monitor;
            synchronized (object) {
                try {
                    while (!workHandle.started) {
                        workHandle.monitor.wait();
                    }
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            return System.currentTimeMillis() - acceptanceTime;
        }
        return -1L;
    }

    private static class DelegatingWorkAdapter
    implements Work {
        private final Work work;
        private final WorkListener workListener;
        private final boolean acceptOnExecution;
        public final Object monitor = new Object();
        public boolean started = false;

        public DelegatingWorkAdapter(Work work, WorkListener workListener, boolean acceptOnExecution) {
            this.work = work;
            this.workListener = workListener;
            this.acceptOnExecution = acceptOnExecution;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            if (this.acceptOnExecution) {
                this.workListener.workAccepted(new WorkEvent((Object)this, 1, this.work, null));
            }
            Object object = this.monitor;
            synchronized (object) {
                this.started = true;
                this.monitor.notify();
            }
            this.workListener.workStarted(new WorkEvent((Object)this, 3, this.work, null));
            try {
                this.work.run();
            }
            catch (Error | RuntimeException ex) {
                this.workListener.workCompleted(new WorkEvent((Object)this, 4, this.work, (WorkException)new WorkCompletedException(ex)));
                throw ex;
            }
            this.workListener.workCompleted(new WorkEvent((Object)this, 4, this.work, null));
        }

        public void release() {
            this.work.release();
        }
    }
}

