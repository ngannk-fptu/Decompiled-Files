/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SynchronyScheduledExecutorServiceProvider
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SynchronyScheduledExecutorServiceProvider.class);
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(CORE_POOL_SIZE, this.getThreadFactory("synchrony-interop-scheduled-executor"));
    private static final int CORE_POOL_SIZE = Integer.getInteger("confluence.synchrony.scheduled.executor.core.pool.size", 3);

    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }

    public void destroy() throws Exception {
        this.executorService.shutdown();
    }

    private ThreadFactory getThreadFactory(String threadNamePrefix) {
        return ThreadFactories.named((String)threadNamePrefix).type(ThreadFactories.Type.DAEMON).uncaughtExceptionHandler((t, e) -> {
            log.warn("{}", (Object)e.getMessage());
            log.debug("Detailed stack trace: ", e);
        }).build();
    }
}

