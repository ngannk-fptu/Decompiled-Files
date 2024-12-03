/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 */
package com.atlassian.confluence.extra.jira.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JiraExecutorFactory {
    private static final int THREAD_POOL_IDE_TIME_SECONDS = Integer.getInteger("jira.executor.idletime.seconds", 60);
    private static final int QUEUE_SIZE = Integer.getInteger("jira.executor.queuesize", 1000);
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;

    public JiraExecutorFactory(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.threadLocalDelegateExecutorFactory = threadLocalDelegateExecutorFactory;
    }

    public ExecutorService newLimitedThreadPool(int maxThreadPoolSize, int maxQueueSize, String name) {
        ThreadPoolExecutor baseService = new ThreadPoolExecutor(maxThreadPoolSize, maxThreadPoolSize, (long)THREAD_POOL_IDE_TIME_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(maxQueueSize), ThreadFactories.named((String)name).type(ThreadFactories.Type.DAEMON).build());
        baseService.allowCoreThreadTimeOut(true);
        return this.threadLocalDelegateExecutorFactory.createExecutorService((ExecutorService)baseService);
    }

    public ExecutorService newLimitedThreadPool(int maxThreadPoolSize, String name) {
        return this.newLimitedThreadPool(maxThreadPoolSize, QUEUE_SIZE, name);
    }
}

