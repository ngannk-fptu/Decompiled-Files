/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ErrorHandler
 */
package org.springframework.scheduling.concurrent;

import java.time.Clock;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

class ReschedulingRunnable
extends DelegatingErrorHandlingRunnable
implements ScheduledFuture<Object> {
    private final Trigger trigger;
    private final SimpleTriggerContext triggerContext;
    private final ScheduledExecutorService executor;
    @Nullable
    private ScheduledFuture<?> currentFuture;
    @Nullable
    private Date scheduledExecutionTime;
    private final Object triggerContextMonitor = new Object();

    public ReschedulingRunnable(Runnable delegate, Trigger trigger, Clock clock, ScheduledExecutorService executor, ErrorHandler errorHandler) {
        super(delegate, errorHandler);
        this.trigger = trigger;
        this.triggerContext = new SimpleTriggerContext(clock);
        this.executor = executor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public ScheduledFuture<?> schedule() {
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            if (this.scheduledExecutionTime == null) {
                return null;
            }
            long delay = this.scheduledExecutionTime.getTime() - this.triggerContext.getClock().millis();
            this.currentFuture = this.executor.schedule(this, delay, TimeUnit.MILLISECONDS);
            return this;
        }
    }

    private ScheduledFuture<?> obtainCurrentFuture() {
        Assert.state((this.currentFuture != null ? 1 : 0) != 0, (String)"No scheduled future");
        return this.currentFuture;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Date actualExecutionTime = new Date(this.triggerContext.getClock().millis());
        super.run();
        Date completionTime = new Date(this.triggerContext.getClock().millis());
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            Assert.state((this.scheduledExecutionTime != null ? 1 : 0) != 0, (String)"No scheduled execution");
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
            if (!this.obtainCurrentFuture().isCancelled()) {
                this.schedule();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            return this.obtainCurrentFuture().cancel(mayInterruptIfRunning);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isCancelled() {
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            return this.obtainCurrentFuture().isCancelled();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isDone() {
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            return this.obtainCurrentFuture().isDone();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        ScheduledFuture<?> curr;
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            curr = this.obtainCurrentFuture();
        }
        return curr.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledFuture<?> curr;
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            curr = this.obtainCurrentFuture();
        }
        return curr.get(timeout, unit);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getDelay(TimeUnit unit) {
        ScheduledFuture<?> curr;
        Object object = this.triggerContextMonitor;
        synchronized (object) {
            curr = this.obtainCurrentFuture();
        }
        return curr.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        long diff = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return diff == 0L ? 0 : (diff < 0L ? -1 : 1);
    }
}

