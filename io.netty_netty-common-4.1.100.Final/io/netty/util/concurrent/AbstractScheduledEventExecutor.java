/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor
extends AbstractEventExecutor {
    private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>(){

        @Override
        public int compare(ScheduledFutureTask<?> o1, ScheduledFutureTask<?> o2) {
            return o1.compareTo(o2);
        }
    };
    private static final long START_TIME = System.nanoTime();
    static final Runnable WAKEUP_TASK = new Runnable(){

        @Override
        public void run() {
        }
    };
    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
    long nextTaskId;

    protected AbstractScheduledEventExecutor() {
    }

    protected AbstractScheduledEventExecutor(EventExecutorGroup parent) {
        super(parent);
    }

    protected long getCurrentTimeNanos() {
        return AbstractScheduledEventExecutor.defaultCurrentTimeNanos();
    }

    @Deprecated
    protected static long nanoTime() {
        return AbstractScheduledEventExecutor.defaultCurrentTimeNanos();
    }

    static long defaultCurrentTimeNanos() {
        return System.nanoTime() - START_TIME;
    }

    static long deadlineNanos(long nanoTime, long delay) {
        long deadlineNanos = nanoTime + delay;
        return deadlineNanos < 0L ? Long.MAX_VALUE : deadlineNanos;
    }

    protected static long deadlineToDelayNanos(long deadlineNanos) {
        return ScheduledFutureTask.deadlineToDelayNanos(AbstractScheduledEventExecutor.defaultCurrentTimeNanos(), deadlineNanos);
    }

    protected static long initialNanoTime() {
        return START_TIME;
    }

    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
        if (this.scheduledTaskQueue == null) {
            this.scheduledTaskQueue = new DefaultPriorityQueue(SCHEDULED_FUTURE_TASK_COMPARATOR, 11);
        }
        return this.scheduledTaskQueue;
    }

    private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue) {
        return queue == null || queue.isEmpty();
    }

    protected void cancelScheduledTasks() {
        ScheduledFutureTask[] scheduledTasks;
        assert (this.inEventLoop());
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (AbstractScheduledEventExecutor.isNullOrEmpty(scheduledTaskQueue)) {
            return;
        }
        for (ScheduledFutureTask task : scheduledTasks = scheduledTaskQueue.toArray(new ScheduledFutureTask[0])) {
            task.cancelWithoutRemove(false);
        }
        scheduledTaskQueue.clearIgnoringIndexes();
    }

    protected final Runnable pollScheduledTask() {
        return this.pollScheduledTask(this.getCurrentTimeNanos());
    }

    protected final Runnable pollScheduledTask(long nanoTime) {
        assert (this.inEventLoop());
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null || scheduledTask.deadlineNanos() - nanoTime > 0L) {
            return null;
        }
        this.scheduledTaskQueue.remove();
        scheduledTask.setConsumed();
        return scheduledTask;
    }

    protected final long nextScheduledTaskNano() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        return scheduledTask != null ? scheduledTask.delayNanos() : -1L;
    }

    protected final long nextScheduledTaskDeadlineNanos() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        return scheduledTask != null ? scheduledTask.deadlineNanos() : -1L;
    }

    final ScheduledFutureTask<?> peekScheduledTask() {
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        return scheduledTaskQueue != null ? (ScheduledFutureTask)scheduledTaskQueue.peek() : null;
    }

    protected final boolean hasScheduledTasks() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        return scheduledTask != null && scheduledTask.deadlineNanos() <= this.getCurrentTimeNanos();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (delay < 0L) {
            delay = 0L;
        }
        this.validateScheduled0(delay, unit);
        return this.schedule(new ScheduledFutureTask(this, command, AbstractScheduledEventExecutor.deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(delay))));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(callable, "callable");
        ObjectUtil.checkNotNull(unit, "unit");
        if (delay < 0L) {
            delay = 0L;
        }
        this.validateScheduled0(delay, unit);
        return this.schedule(new ScheduledFutureTask<V>(this, callable, AbstractScheduledEventExecutor.deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(delay))));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (period <= 0L) {
            throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", period));
        }
        this.validateScheduled0(initialDelay, unit);
        this.validateScheduled0(period, unit);
        return this.schedule(new ScheduledFutureTask(this, command, AbstractScheduledEventExecutor.deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(initialDelay)), unit.toNanos(period)));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (delay <= 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", delay));
        }
        this.validateScheduled0(initialDelay, unit);
        this.validateScheduled0(delay, unit);
        return this.schedule(new ScheduledFutureTask(this, command, AbstractScheduledEventExecutor.deadlineNanos(this.getCurrentTimeNanos(), unit.toNanos(initialDelay)), -unit.toNanos(delay)));
    }

    private void validateScheduled0(long amount, TimeUnit unit) {
        this.validateScheduled(amount, unit);
    }

    @Deprecated
    protected void validateScheduled(long amount, TimeUnit unit) {
    }

    final void scheduleFromEventLoop(ScheduledFutureTask<?> task) {
        this.scheduledTaskQueue().add(task.setId(++this.nextTaskId));
    }

    private <V> ScheduledFuture<V> schedule(ScheduledFutureTask<V> task) {
        if (this.inEventLoop()) {
            this.scheduleFromEventLoop(task);
        } else {
            long deadlineNanos = task.deadlineNanos();
            if (this.beforeScheduledTaskSubmitted(deadlineNanos)) {
                this.execute(task);
            } else {
                this.lazyExecute(task);
                if (this.afterScheduledTaskSubmitted(deadlineNanos)) {
                    this.execute(WAKEUP_TASK);
                }
            }
        }
        return task;
    }

    final void removeScheduled(ScheduledFutureTask<?> task) {
        assert (task.isCancelled());
        if (this.inEventLoop()) {
            this.scheduledTaskQueue().removeTyped(task);
        } else {
            this.lazyExecute(task);
        }
    }

    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        return true;
    }

    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        return true;
    }
}

