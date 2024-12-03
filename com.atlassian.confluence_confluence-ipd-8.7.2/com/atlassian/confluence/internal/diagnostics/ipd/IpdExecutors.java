/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd;

import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpdExecutors {
    private static final Logger log = LoggerFactory.getLogger(IpdExecutors.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private final ConcurrentMap<String, ExecutorService> ipdExecutorsByName = new ConcurrentHashMap<String, ExecutorService>();
    private final EventPublisher eventPublisher;

    public ExecutorService createSingleTaskExecutorService(String threadName) {
        return this.registerExecutor(threadName, new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), ThreadFactories.namedThreadFactory((String)threadName)));
    }

    public IpdExecutors(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationStopping(ApplicationStoppingEvent applicationStoppingEvent) {
        this.shutdownIpdExecutors();
        this.eventPublisher.unregister((Object)this);
    }

    public ScheduledExecutorService createSingleThreadScheduledExecutorService(String threadName) {
        return this.registerExecutor(threadName, Executors.newSingleThreadScheduledExecutor(ThreadFactories.namedThreadFactory((String)threadName)));
    }

    private <T extends ExecutorService> T registerExecutor(String executorName, T executor) {
        if (this.ipdExecutorsByName.putIfAbsent(executorName, executor) != null) {
            throw new IllegalStateException("Executor with name " + executorName + " already exists");
        }
        return executor;
    }

    private void shutdownIpdExecutors() {
        this.ipdExecutorsByName.forEach(this::shutdownExecutor);
        this.ipdExecutorsByName.clear();
    }

    private void shutdownExecutor(String executorName, ExecutorService executor) {
        try {
            executor.shutdown();
            log.info("Shutdown IPD executor {}", (Object)executorName);
            if (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
                    log.debug("Failed to terminate IPD executor {}", (Object)executorName);
                }
            }
        }
        catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            log.error("Failed to shutdown IPD executor {}", (Object)executorName, (Object)e);
        }
    }
}

