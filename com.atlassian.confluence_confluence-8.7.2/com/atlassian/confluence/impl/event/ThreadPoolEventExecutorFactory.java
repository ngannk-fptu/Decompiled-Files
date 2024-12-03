/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.spi.EventExecutorFactory
 *  io.atlassian.util.concurrent.Lazy
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.event;

import com.atlassian.confluence.event.CpuBasedThreadPoolConfiguration;
import com.atlassian.confluence.event.MonitorableCallerRunsPolicy;
import com.atlassian.confluence.impl.event.DurationChecker;
import com.atlassian.event.spi.EventExecutorFactory;
import io.atlassian.util.concurrent.Lazy;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;

final class ThreadPoolEventExecutorFactory
implements EventExecutorFactory {
    private final CpuBasedThreadPoolConfiguration configuration = new CpuBasedThreadPoolConfiguration();
    private final ResettableLazyReference<ExecutorService> executorRef = Lazy.resettable(() -> new ThreadPoolExecutor(this.configuration.getCorePoolSize(), this.configuration.getMaximumPoolSize(), this.configuration.getKeepAliveTime(), this.configuration.getTimeUnit(), new LinkedBlockingQueue<Runnable>(this.configuration.getQueueSize()), threadFactory, new MonitorableCallerRunsPolicy(new DurationChecker(60))));

    public ThreadPoolEventExecutorFactory(ThreadFactory threadFactory) {
    }

    @PreDestroy
    void shutdown() {
        if (this.executorRef.isInitialized()) {
            ((ExecutorService)this.executorRef.get()).shutdown();
            this.executorRef.reset();
        }
    }

    @Nonnull
    public ExecutorService getExecutor() {
        return (ExecutorService)this.executorRef.get();
    }
}

