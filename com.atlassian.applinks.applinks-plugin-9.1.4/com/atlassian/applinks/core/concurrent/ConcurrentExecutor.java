/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.concurrent;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConcurrentExecutor
implements DisposableBean {
    private final ExecutorService executor;

    @Autowired
    public ConcurrentExecutor(ThreadLocalDelegateExecutorFactory delegateExecutorFactory) {
        this.executor = delegateExecutorFactory.createExecutorService(Executors.newCachedThreadPool(new DefaultThreadFactory("AppLinks ConcurrentExecutor")));
    }

    public void destroy() throws Exception {
        this.executor.shutdown();
    }

    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks) throws InterruptedException {
        return this.executor.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.executor.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.executor.invokeAny(tasks, timeout, unit);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.executor.submit(task);
    }

    public Future<?> submit(Runnable task) {
        return this.executor.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return this.executor.submit(task, result);
    }

    public void execute(Runnable command) {
        this.executor.execute(command);
    }

    private static class DefaultThreadFactory
    implements ThreadFactory {
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        private DefaultThreadFactory(String name) {
            this.namePrefix = Objects.requireNonNull(name, "name") + ":thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(null, r, this.namePrefix + this.threadNumber.getAndIncrement());
        }
    }
}

