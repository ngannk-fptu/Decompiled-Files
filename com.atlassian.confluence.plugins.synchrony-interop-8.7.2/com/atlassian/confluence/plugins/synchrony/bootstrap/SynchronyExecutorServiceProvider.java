/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyRejectedExecutionHandler;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
class SynchronyExecutorServiceProvider
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SynchronyExecutorServiceProvider.class);
    private final ExecutorService executorService;
    private static final int DEFAULT_CORE_POOL_SIZE = Integer.getInteger("confluence.synchrony.executor.core.pool.size", 0);
    private static final int DEFAULT_MAX_POOL_SIZE = Integer.getInteger("confluence.synchrony.executor.max.pool.size", 10);

    public SynchronyExecutorServiceProvider() {
        this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, new SynchronyRejectedExecutionHandler());
    }

    @VisibleForTesting
    SynchronyExecutorServiceProvider(int corePoolSize, int maxPoolSize, RejectedExecutionHandler handler) {
        this.executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), this.getThreadFactory("synchrony-interop-executor"), handler);
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public void destroy() throws Exception {
        this.executorService.shutdownNow();
    }

    private ThreadFactory getThreadFactory(String threadNamePrefix) {
        return ThreadFactories.named((String)threadNamePrefix).type(ThreadFactories.Type.DAEMON).uncaughtExceptionHandler((t, e) -> {
            log.warn("{}", (Object)e.getMessage());
            log.debug("Detailed stack trace: ", e);
        }).build();
    }
}

