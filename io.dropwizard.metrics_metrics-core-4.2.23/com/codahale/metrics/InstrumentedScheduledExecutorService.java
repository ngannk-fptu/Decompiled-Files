/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentedScheduledExecutorService
implements ScheduledExecutorService {
    private static final AtomicLong NAME_COUNTER = new AtomicLong();
    private final ScheduledExecutorService delegate;
    private final Meter submitted;
    private final Counter running;
    private final Meter completed;
    private final Timer duration;
    private final Meter scheduledOnce;
    private final Meter scheduledRepetitively;
    private final Counter scheduledOverrun;
    private final Histogram percentOfPeriod;

    public InstrumentedScheduledExecutorService(ScheduledExecutorService delegate, MetricRegistry registry) {
        this(delegate, registry, "instrumented-scheduled-executor-service-" + NAME_COUNTER.incrementAndGet());
    }

    public InstrumentedScheduledExecutorService(ScheduledExecutorService delegate, MetricRegistry registry, String name) {
        this.delegate = delegate;
        this.submitted = registry.meter(MetricRegistry.name(name, "submitted"));
        this.running = registry.counter(MetricRegistry.name(name, "running"));
        this.completed = registry.meter(MetricRegistry.name(name, "completed"));
        this.duration = registry.timer(MetricRegistry.name(name, "duration"));
        this.scheduledOnce = registry.meter(MetricRegistry.name(name, "scheduled.once"));
        this.scheduledRepetitively = registry.meter(MetricRegistry.name(name, "scheduled.repetitively"));
        this.scheduledOverrun = registry.counter(MetricRegistry.name(name, "scheduled.overrun"));
        this.percentOfPeriod = registry.histogram(MetricRegistry.name(name, "scheduled.percent-of-period"));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        this.scheduledOnce.mark();
        return this.delegate.schedule(new InstrumentedRunnable(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        this.scheduledOnce.mark();
        return this.delegate.schedule(new InstrumentedCallable<V>(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        this.scheduledRepetitively.mark();
        return this.delegate.scheduleAtFixedRate(new InstrumentedPeriodicRunnable(command, period, unit), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        this.scheduledRepetitively.mark();
        return this.delegate.scheduleWithFixedDelay(new InstrumentedRunnable(command), initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        this.submitted.mark();
        return this.delegate.submit(new InstrumentedCallable<T>(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        this.submitted.mark();
        return this.delegate.submit(new InstrumentedRunnable(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        this.submitted.mark();
        return this.delegate.submit(new InstrumentedRunnable(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        this.submitted.mark(tasks.size());
        Collection<Callable<T>> instrumented = this.instrument(tasks);
        return this.delegate.invokeAll(instrumented);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        this.submitted.mark(tasks.size());
        Collection<Callable<T>> instrumented = this.instrument(tasks);
        return this.delegate.invokeAll(instrumented, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        this.submitted.mark(tasks.size());
        Collection<Callable<T>> instrumented = this.instrument(tasks);
        return this.delegate.invokeAny(instrumented);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.submitted.mark(tasks.size());
        Collection<Callable<T>> instrumented = this.instrument(tasks);
        return this.delegate.invokeAny(instrumented, timeout, unit);
    }

    private <T> Collection<? extends Callable<T>> instrument(Collection<? extends Callable<T>> tasks) {
        ArrayList<InstrumentedCallable<T>> instrumented = new ArrayList<InstrumentedCallable<T>>(tasks.size());
        for (Callable<T> task : tasks) {
            instrumented.add(new InstrumentedCallable<T>(task));
        }
        return instrumented;
    }

    @Override
    public void execute(Runnable command) {
        this.submitted.mark();
        this.delegate.execute(new InstrumentedRunnable(command));
    }

    private class InstrumentedRunnable
    implements Runnable {
        private final Runnable command;

        InstrumentedRunnable(Runnable command) {
            this.command = command;
        }

        @Override
        public void run() {
            InstrumentedScheduledExecutorService.this.running.inc();
            Timer.Context context = InstrumentedScheduledExecutorService.this.duration.time();
            try {
                this.command.run();
            }
            finally {
                context.stop();
                InstrumentedScheduledExecutorService.this.running.dec();
                InstrumentedScheduledExecutorService.this.completed.mark();
            }
        }
    }

    private class InstrumentedCallable<T>
    implements Callable<T> {
        private final Callable<T> task;

        InstrumentedCallable(Callable<T> task) {
            this.task = task;
        }

        @Override
        public T call() throws Exception {
            InstrumentedScheduledExecutorService.this.running.inc();
            Timer.Context context = InstrumentedScheduledExecutorService.this.duration.time();
            try {
                T t = this.task.call();
                return t;
            }
            finally {
                context.stop();
                InstrumentedScheduledExecutorService.this.running.dec();
                InstrumentedScheduledExecutorService.this.completed.mark();
            }
        }
    }

    private class InstrumentedPeriodicRunnable
    implements Runnable {
        private final Runnable command;
        private final long periodInNanos;

        InstrumentedPeriodicRunnable(Runnable command, long period, TimeUnit unit) {
            this.command = command;
            this.periodInNanos = unit.toNanos(period);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            InstrumentedScheduledExecutorService.this.running.inc();
            Timer.Context context = InstrumentedScheduledExecutorService.this.duration.time();
            try {
                this.command.run();
            }
            finally {
                long elapsed = context.stop();
                InstrumentedScheduledExecutorService.this.running.dec();
                InstrumentedScheduledExecutorService.this.completed.mark();
                if (elapsed > this.periodInNanos) {
                    InstrumentedScheduledExecutorService.this.scheduledOverrun.inc();
                }
                InstrumentedScheduledExecutorService.this.percentOfPeriod.update(100L * elapsed / this.periodInNanos);
            }
        }
    }
}

