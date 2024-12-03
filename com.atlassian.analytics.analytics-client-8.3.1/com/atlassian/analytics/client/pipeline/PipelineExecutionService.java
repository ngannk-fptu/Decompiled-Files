/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.analytics.client.pipeline;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class PipelineExecutionService
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(PipelineExecutionService.class);
    private static final int EVENT_QUEUE_CAPACITY = 10000;
    private static final String REJECTED_EXECUTION_MESSAGE = "Analytics event not be processed (most likely due to too many events in queue for single-threaded event processor).";
    private final ExecutorService executorService;

    public PipelineExecutionService(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.executorService = this.createExecutorService(threadLocalDelegateExecutorFactory);
    }

    @VisibleForTesting
    public PipelineExecutionService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Future submit(Runnable task) {
        return this.executorService.submit(task);
    }

    private ExecutorService createExecutorService(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        return threadLocalDelegateExecutorFactory.createExecutorService((ExecutorService)new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000), ThreadFactories.namedThreadFactory((String)"analyticsEventProcessor", (ThreadFactories.Type)ThreadFactories.Type.DAEMON), (r, executor) -> log.warn(REJECTED_EXECUTION_MESSAGE)));
    }

    public void destroy() {
        this.executorService.shutdown();
    }
}

