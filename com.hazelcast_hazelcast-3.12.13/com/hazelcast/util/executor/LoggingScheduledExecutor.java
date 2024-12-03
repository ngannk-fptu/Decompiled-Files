/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.executor;

import com.hazelcast.logging.ILogger;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class LoggingScheduledExecutor
extends ScheduledThreadPoolExecutor {
    boolean manualRemoveOnCancel;
    private final ILogger logger;
    private volatile boolean shutdownInitiated;

    public LoggingScheduledExecutor(ILogger logger, int corePoolSize, ThreadFactory threadFactory) {
        this(logger, corePoolSize, threadFactory, false);
    }

    public LoggingScheduledExecutor(ILogger logger, int corePoolSize, ThreadFactory threadFactory, boolean removeOnCancel) {
        super(corePoolSize, threadFactory);
        this.logger = Preconditions.checkNotNull(logger, "logger cannot be null");
        this.manualRemoveOnCancel = this.manualRemoveOnCancel(removeOnCancel);
    }

    public LoggingScheduledExecutor(ILogger logger, int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this(logger, corePoolSize, threadFactory, false, handler);
    }

    public LoggingScheduledExecutor(ILogger logger, int corePoolSize, ThreadFactory threadFactory, boolean removeOnCancel, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
        this.logger = Preconditions.checkNotNull(logger, "logger cannot be null");
        this.manualRemoveOnCancel = this.manualRemoveOnCancel(removeOnCancel);
    }

    private boolean manualRemoveOnCancel(boolean removeOnCancel) {
        if (this.trySetRemoveOnCancelPolicy()) {
            return false;
        }
        return removeOnCancel;
    }

    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"})
    private boolean trySetRemoveOnCancelPolicy() {
        try {
            Method method = ScheduledThreadPoolExecutor.class.getMethod("setRemoveOnCancelPolicy", Boolean.TYPE);
            method.invoke((Object)this, true);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        if (!this.manualRemoveOnCancel) {
            return super.decorateTask(runnable, task);
        }
        return new RemoveOnCancelFuture<V>(runnable, task, this);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        if (!this.manualRemoveOnCancel) {
            return super.decorateTask(callable, task);
        }
        return new RemoveOnCancelFuture<V>(callable, task, this);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);
        Level level = Level.FINE;
        if (throwable == null && runnable instanceof ScheduledFuture && ((ScheduledFuture)((Object)runnable)).isDone()) {
            try {
                ((Future)((Object)runnable)).get();
            }
            catch (CancellationException ce) {
                throwable = ce;
            }
            catch (ExecutionException ee) {
                level = Level.SEVERE;
                throwable = ee.getCause();
            }
            catch (InterruptedException ie) {
                throwable = ie;
                Thread.currentThread().interrupt();
            }
        }
        if (throwable instanceof RejectedExecutionException && this.shutdownInitiated) {
            level = Level.FINE;
        }
        if (throwable != null) {
            this.logger.log(level, "Failed to execute " + runnable, throwable);
        }
    }

    public void notifyShutdownInitiated() {
        this.shutdownInitiated = true;
    }

    static class RemoveOnCancelFuture<V>
    implements RunnableScheduledFuture<V> {
        private final Object task;
        private final RunnableScheduledFuture<V> delegate;
        private final LoggingScheduledExecutor executor;

        RemoveOnCancelFuture(Object task, RunnableScheduledFuture<V> delegate, LoggingScheduledExecutor executor) {
            this.task = task;
            this.delegate = delegate;
            this.executor = executor;
        }

        @Override
        public boolean isPeriodic() {
            return this.delegate.isPeriodic();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return this.delegate.getDelay(unit);
        }

        @Override
        public void run() {
            this.delegate.run();
        }

        @Override
        public int compareTo(Delayed o) {
            return this.delegate.compareTo(o);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RemoveOnCancelFuture)) {
                return false;
            }
            RemoveOnCancelFuture that = (RemoveOnCancelFuture)o;
            return this.delegate.equals(that.delegate);
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean removeOnCancel = !this.executor.isShutdown();
            boolean cancelled = this.delegate.cancel(mayInterruptIfRunning);
            if (cancelled && removeOnCancel) {
                this.executor.remove(this);
            }
            return cancelled;
        }

        @Override
        public boolean isCancelled() {
            return this.delegate.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.delegate.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return this.delegate.get();
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.delegate.get(timeout, unit);
        }

        public String toString() {
            return "RemoveOnCancelFuture{task=" + this.task + '}';
        }
    }
}

