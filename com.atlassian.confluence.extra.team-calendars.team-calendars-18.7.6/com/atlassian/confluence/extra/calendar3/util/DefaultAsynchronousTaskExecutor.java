/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultAsynchronousTaskExecutor
implements AsynchronousTaskExecutor,
InitializingBean,
DisposableBean {
    private static Logger LOGGER = LoggerFactory.getLogger(DefaultAsynchronousTaskExecutor.class);
    private static final int MAX_CONCURRENT_TASKS = Integer.getInteger("com.atlassian.confluence.extra.calendar3.concurrent.task.max", 5);
    private static final int CONCURRENT_QUEUE_SIZE = Integer.getInteger("com.atlassian.confluence.extra.calendar3.concurrent.queue.size", 2000);
    private static final boolean ENABLED = BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.concurrent.task.enabled", Boolean.TRUE.toString()));
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    private ExecutorService executorService;

    @Autowired
    public DefaultAsynchronousTaskExecutor(@ComponentImport ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.threadLocalDelegateExecutorFactory = threadLocalDelegateExecutorFactory;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public void destroy() throws Exception {
        if (ENABLED) {
            this.executorService.shutdownNow();
        }
    }

    public void afterPropertiesSet() throws Exception {
        if (ENABLED) {
            ThreadPoolExecutor.DiscardPolicy handler = new ThreadPoolExecutor.DiscardPolicy();
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(MAX_CONCURRENT_TASKS, MAX_CONCURRENT_TASKS, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(CONCURRENT_QUEUE_SIZE), new DelegatingThreadFactory(Executors.defaultThreadFactory()), handler);
            this.executorService = this.threadLocalDelegateExecutorFactory.createExecutorService((ExecutorService)threadPool);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (!ENABLED) {
            throw new IllegalStateException(String.format("Worker threads for Team Calendars not enabled. To enable it, please set the property %s", "com.atlassian.confluence.extra.calendar3.concurrent.task.enabled"));
        }
        long startTime = System.currentTimeMillis();
        return this.executorService.submit(() -> {
            long queueDuration = System.currentTimeMillis() - startTime;
            LOGGER.debug("Task {} spent {}ms in queue", (Object)task, (Object)queueDuration);
            return this.wrap(task, this.clientTrace(), Thread.currentThread().getName()).call();
        });
    }

    private <T> Callable<T> wrap(Callable<T> task, Exception clientStack, String clientThreadName) throws Exception {
        return () -> {
            try {
                return task.call();
            }
            catch (Exception e) {
                LOGGER.error("Exception happens on task execution:", (Throwable)e);
                LOGGER.error("Exception in task submitted from thread {} here:", (Object)clientThreadName, (Object)clientStack);
                throw e;
            }
        };
    }

    private Exception clientTrace() {
        return new Exception("Client stack trace");
    }

    private static class DelegatingThreadFactory
    implements ThreadFactory {
        private static final String NAME_PREFIX = "team-calendars-worker-";
        private final ThreadFactory delegate;
        private int threadNumber;

        private DelegatingThreadFactory(ThreadFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread newThread = this.delegate.newThread(runnable);
            newThread.setName(NAME_PREFIX + this.threadNumber++);
            return newThread;
        }
    }
}

