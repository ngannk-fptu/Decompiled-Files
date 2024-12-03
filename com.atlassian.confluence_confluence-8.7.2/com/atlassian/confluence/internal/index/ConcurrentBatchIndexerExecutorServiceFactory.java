/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.confluence.upgrade.UpgradeTask;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConcurrentBatchIndexerExecutorServiceFactory {
    private static final int MAX_THREAD_COUNT = 50;

    public ExecutorService get(List<HibernateHandle> handles, int threadCount) {
        int poolSize = ConcurrentBatchIndexerExecutorServiceFactory.calculateNumberOfThreads(handles, threadCount);
        UpgradeTask.log.info("Starting thread pool with {} thread(s)", (Object)poolSize);
        ExecutorService executor = ConfluenceExecutors.newFixedThreadPool(poolSize, ConcurrentBatchIndexerExecutorServiceFactory.getThreadFactory());
        return executor;
    }

    @VisibleForTesting
    static ThreadFactory getThreadFactory() {
        return new ThreadFactory(){
            private final AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable command) {
                String threadName = String.format("%s: %d", "Indexer", this.count.incrementAndGet());
                UpgradeTask.log.debug("Creating indexer thread [{}]", (Object)threadName);
                Thread thread = new Thread(command, threadName);
                thread.setUncaughtExceptionHandler((t, e) -> UpgradeTask.log.error("Uncaught exception: " + e.getMessage(), e));
                return thread;
            }
        };
    }

    static int calculateNumberOfThreads(List<HibernateHandle> handles, Integer specifiedThreadCount) {
        return ConcurrentBatchIndexerExecutorServiceFactory.calculateNumberOfThreads(handles.size(), Runtime.getRuntime().availableProcessors(), specifiedThreadCount);
    }

    static int calculateNumberOfThreads(int numObjects, int availableProcessors, @Nullable Integer specifiedThreadCount) {
        int threadCount;
        if (specifiedThreadCount != null) {
            threadCount = specifiedThreadCount;
        } else {
            if (numObjects < 100) {
                return 1;
            }
            double uCPU = 0.5;
            double WC = 0.8;
            double nThreads = (double)availableProcessors * uCPU * (1.0 + WC);
            threadCount = (int)Math.ceil(nThreads);
        }
        return Math.min(50, threadCount);
    }
}

