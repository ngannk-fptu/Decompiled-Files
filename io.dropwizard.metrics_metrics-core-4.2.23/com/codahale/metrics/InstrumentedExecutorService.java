/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentedExecutorService
implements ExecutorService {
    private static final AtomicLong NAME_COUNTER = new AtomicLong();
    private final ExecutorService delegate;
    private final Meter submitted;
    private final Counter running;
    private final Meter completed;
    private final Timer idle;
    private final Timer duration;

    public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry) {
        this(delegate, registry, "instrumented-delegate-" + NAME_COUNTER.incrementAndGet());
    }

    public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry, String name) {
        this.delegate = delegate;
        this.submitted = registry.meter(MetricRegistry.name(name, "submitted"));
        this.running = registry.counter(MetricRegistry.name(name, "running"));
        this.completed = registry.meter(MetricRegistry.name(name, "completed"));
        this.idle = registry.timer(MetricRegistry.name(name, "idle"));
        this.duration = registry.timer(MetricRegistry.name(name, "duration"));
        if (delegate instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor)delegate;
            registry.registerGauge(MetricRegistry.name(name, "pool.size"), executor::getPoolSize);
            registry.registerGauge(MetricRegistry.name(name, "pool.core"), executor::getCorePoolSize);
            registry.registerGauge(MetricRegistry.name(name, "pool.max"), executor::getMaximumPoolSize);
            BlockingQueue<Runnable> queue = executor.getQueue();
            registry.registerGauge(MetricRegistry.name(name, "tasks.active"), executor::getActiveCount);
            registry.registerGauge(MetricRegistry.name(name, "tasks.completed"), executor::getCompletedTaskCount);
            registry.registerGauge(MetricRegistry.name(name, "tasks.queued"), queue::size);
            registry.registerGauge(MetricRegistry.name(name, "tasks.capacity"), queue::remainingCapacity);
        } else if (delegate instanceof ForkJoinPool) {
            ForkJoinPool forkJoinPool = (ForkJoinPool)delegate;
            registry.registerGauge(MetricRegistry.name(name, "tasks.stolen"), forkJoinPool::getStealCount);
            registry.registerGauge(MetricRegistry.name(name, "tasks.queued"), forkJoinPool::getQueuedTaskCount);
            registry.registerGauge(MetricRegistry.name(name, "threads.active"), forkJoinPool::getActiveThreadCount);
            registry.registerGauge(MetricRegistry.name(name, "threads.running"), forkJoinPool::getRunningThreadCount);
        }
    }

    @Override
    public void execute(Runnable runnable) {
        this.submitted.mark();
        this.delegate.execute(new InstrumentedRunnable(runnable));
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        this.submitted.mark();
        return this.delegate.submit(new InstrumentedRunnable(runnable));
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T result) {
        this.submitted.mark();
        return this.delegate.submit(new InstrumentedRunnable(runnable), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        this.submitted.mark();
        return this.delegate.submit(new InstrumentedCallable<T>(task));
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
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
        this.submitted.mark(tasks.size());
        Collection<Callable<T>> instrumented = this.instrument(tasks);
        return this.delegate.invokeAny(instrumented);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
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
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return this.delegate.awaitTermination(l, timeUnit);
    }

    private class InstrumentedRunnable
    implements Runnable {
        private final Runnable task;
        private final Timer.Context idleContext;

        InstrumentedRunnable(Runnable task) {
            this.task = task;
            this.idleContext = InstrumentedExecutorService.this.idle.time();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            this.idleContext.stop();
            InstrumentedExecutorService.this.running.inc();
            try (Timer.Context durationContext = InstrumentedExecutorService.this.duration.time();){
                this.task.run();
            }
            finally {
                InstrumentedExecutorService.this.running.dec();
                InstrumentedExecutorService.this.completed.mark();
            }
        }
    }

    private class InstrumentedCallable<T>
    implements Callable<T> {
        private final Callable<T> callable;
        private final Timer.Context idleContext;

        InstrumentedCallable(Callable<T> callable) {
            this.callable = callable;
            this.idleContext = InstrumentedExecutorService.this.idle.time();
        }

        @Override
        public T call() throws Exception {
            this.idleContext.stop();
            InstrumentedExecutorService.this.running.inc();
            try {
                T t;
                block9: {
                    Timer.Context context = InstrumentedExecutorService.this.duration.time();
                    try {
                        t = this.callable.call();
                        if (context == null) break block9;
                        context.close();
                    }
                    catch (Throwable throwable) {
                        if (context != null) {
                            try {
                                context.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                }
                return t;
            }
            finally {
                InstrumentedExecutorService.this.running.dec();
                InstrumentedExecutorService.this.completed.mark();
            }
        }
    }
}

