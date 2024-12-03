/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.internal.TimedCallable;
import io.micrometer.core.instrument.internal.TimedRunnable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class TimedExecutorService
implements ExecutorService {
    private final MeterRegistry registry;
    private final ExecutorService delegate;
    private final Timer executionTimer;
    private final Timer idleTimer;

    public TimedExecutorService(MeterRegistry registry, ExecutorService delegate, String executorServiceName, String metricPrefix, Iterable<Tag> tags) {
        this.registry = registry;
        this.delegate = delegate;
        Tags finalTags = Tags.concat(tags, "name", executorServiceName);
        this.executionTimer = registry.timer(metricPrefix + "executor", finalTags);
        this.idleTimer = registry.timer(metricPrefix + "executor.idle", finalTags);
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
        return this.delegate.submit(this.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.delegate.submit(this.wrap(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.delegate.submit(this.wrap(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(this.wrapAll(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(this.wrapAll(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(this.wrapAll(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(this.wrapAll(tasks), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.delegate.execute(this.wrap(command));
    }

    private Runnable wrap(Runnable task) {
        return new TimedRunnable(this.registry, this.executionTimer, this.idleTimer, task);
    }

    private <T> Callable<T> wrap(Callable<T> task) {
        return new TimedCallable<T>(this.registry, this.executionTimer, this.idleTimer, task);
    }

    private <T> Collection<? extends Callable<T>> wrapAll(Collection<? extends Callable<T>> tasks) {
        return tasks.stream().map(this::wrap).collect(Collectors.toList());
    }
}

