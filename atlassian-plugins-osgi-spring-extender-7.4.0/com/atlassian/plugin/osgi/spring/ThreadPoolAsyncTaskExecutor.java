/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.task.AsyncTaskExecutor
 */
package com.atlassian.plugin.osgi.spring;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

public class ThreadPoolAsyncTaskExecutor
implements AsyncTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolAsyncTaskExecutor.class);
    private final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory());

    public void execute(@Nonnull Runnable task, long startTimeout) {
        this.executor.execute(task);
    }

    @Nonnull
    public Future<?> submit(@Nonnull Runnable task) {
        return this.executor.submit(task);
    }

    @Nonnull
    public <T> Future<T> submit(@Nonnull Callable<T> task) {
        return this.executor.submit(task);
    }

    public void execute(@Nonnull Runnable task) {
        this.execute(task, -1L);
    }

    public void shutdown() {
        log.debug("Attempting to shutdown ExecutorService");
        this.executor.shutdown();
        try {
            if (this.executor.awaitTermination(5L, TimeUnit.SECONDS)) {
                log.debug("ExecutorService has shutdown gracefully");
            } else {
                log.warn("ExecutorService did not shutdown within the timeout; forcing shutdown");
                this.executor.shutdownNow();
                if (this.executor.awaitTermination(5L, TimeUnit.SECONDS)) {
                    log.debug("ExecutorService has been forced to shutdown");
                } else {
                    log.warn("ExecutorService did not shutdown; it will be abandoned");
                }
            }
        }
        catch (InterruptedException e) {
            log.warn("Interrupted while waiting for the executor service to shutdown; some worker threads may still be running");
            Thread.currentThread().interrupt();
        }
    }

    private static class NamedThreadFactory
    implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger();

        private NamedThreadFactory() {
        }

        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("ThreadPoolAsyncTaskExecutor::Thread " + this.counter.incrementAndGet());
            return thread;
        }
    }
}

