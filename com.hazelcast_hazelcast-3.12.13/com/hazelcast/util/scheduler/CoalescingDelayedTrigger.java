/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.scheduler;

import com.hazelcast.spi.ExecutionService;
import com.hazelcast.util.Clock;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CoalescingDelayedTrigger {
    private final ExecutionService executionService;
    private final long delay;
    private final long maxDelay;
    private final Runnable runnable;
    private long hardLimit;
    private ScheduledFuture<?> future;

    public CoalescingDelayedTrigger(ExecutionService executionService, long delay, long maxDelay, Runnable runnable) {
        if (delay <= 0L) {
            throw new IllegalArgumentException("Delay must be a positive number. Delay: " + delay);
        }
        if (maxDelay < delay) {
            throw new IllegalArgumentException("Maximum delay must be greater or equal than delay. Maximum delay: " + maxDelay + ", Delay: " + delay);
        }
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        this.executionService = executionService;
        this.delay = delay;
        this.maxDelay = maxDelay;
        this.runnable = runnable;
    }

    public void executeWithDelay() {
        long now = Clock.currentTimeMillis();
        if (this.delay + now > this.hardLimit) {
            this.scheduleNewExecution(now);
        } else if (!this.tryPostponeExecution()) {
            this.scheduleNewExecution(now);
        }
    }

    private boolean tryPostponeExecution() {
        boolean cancel = this.future.cancel(false);
        if (!cancel) {
            return false;
        }
        this.future = this.executionService.schedule(this.runnable, this.delay, TimeUnit.MILLISECONDS);
        return true;
    }

    private void scheduleNewExecution(long now) {
        this.future = this.executionService.schedule(this.runnable, this.delay, TimeUnit.MILLISECONDS);
        this.hardLimit = now + this.maxDelay;
    }
}

