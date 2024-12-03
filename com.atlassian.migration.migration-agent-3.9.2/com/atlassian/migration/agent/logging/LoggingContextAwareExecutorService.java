/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.collections.MapUtils
 *  org.slf4j.MDC
 */
package com.atlassian.migration.agent.logging;

import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.collections.MapUtils;
import org.slf4j.MDC;

@ParametersAreNonnullByDefault
public class LoggingContextAwareExecutorService
implements ExecutorService {
    private final ExecutorService delegate;

    public LoggingContextAwareExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
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
        Map ctx = MDC.getCopyOfContextMap();
        return this.delegate.submit(() -> LoggingContextBuilder.logCtx().withContext(ctx).executeCallable(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Map ctx = MDC.getCopyOfContextMap();
        return this.delegate.submit(() -> LoggingContextBuilder.logCtx().withContext(ctx).execute(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        Map ctx = MDC.getCopyOfContextMap();
        return this.delegate.submit(() -> LoggingContextBuilder.logCtx().withContext(ctx).execute(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(LoggingContextAwareExecutorService.addLoggingContextToTasks(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(LoggingContextAwareExecutorService.addLoggingContextToTasks(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(LoggingContextAwareExecutorService.addLoggingContextToTasks(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(LoggingContextAwareExecutorService.addLoggingContextToTasks(tasks), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        Map ctx = MDC.getCopyOfContextMap();
        this.delegate.execute(() -> {
            LoggingContextAwareExecutorService.setLoggingContext(ctx);
            command.run();
        });
    }

    private static <T> Collection<? extends Callable<T>> addLoggingContextToTasks(Collection<? extends Callable<T>> tasks) {
        Map ctx = MDC.getCopyOfContextMap();
        return tasks.stream().map(task -> () -> {
            LoggingContextAwareExecutorService.setLoggingContext(ctx);
            return task.call();
        }).collect(Collectors.toList());
    }

    private static void setLoggingContext(Map<String, String> ctx) {
        if (MapUtils.isNotEmpty(ctx)) {
            MDC.setContextMap(ctx);
        }
    }
}

