/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.collect.Lists
 *  org.apache.commons.io.FileUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index;

import bucket.core.persistence.hibernate.HibernateHandle;
import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.internal.index.BatchIndexer;
import com.atlassian.confluence.internal.index.ConcurrentBatchIndexerExecutorServiceFactory;
import com.atlassian.confluence.internal.index.ReindexProgress;
import com.atlassian.confluence.util.Progress;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class ConcurrentBatchIndexer
implements BatchIndexer {
    private static final Logger log = LoggerFactory.getLogger(ConcurrentBatchIndexer.class);
    private final BatchIndexer delegate;
    private final Integer threadCount;
    private final ConcurrentBatchIndexerExecutorServiceFactory executorServiceFactory;

    public ConcurrentBatchIndexer(BatchIndexer delegate, Integer threadCount, ConcurrentBatchIndexerExecutorServiceFactory executorServiceFactory) {
        this.delegate = delegate;
        this.threadCount = threadCount;
        this.executorServiceFactory = executorServiceFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void index(List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle> handles, ReindexProgress progress) {
        ExecutorService executor = this.executorServiceFactory.get(handles, this.threadCount);
        try {
            List<Future<?>> batches = this.submitBatches(executor, handles, progress);
            int total = batches.size();
            int current = 0;
            for (Future<?> future : batches) {
                log.debug("Waiting for work batches {}/{}", (Object)(++current), (Object)total);
                try {
                    future.get();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Cancelling waiting for work batches {}/{}", (Object)current, (Object)total);
                    log.info("Shutting down indexing thread pool");
                    executor.shutdownNow();
                    return;
                }
                catch (ExecutionException e) {
                    try {
                        log.error("Exception processing batch", (Throwable)e);
                    }
                    catch (Throwable throwable) {
                        throw throwable;
                        return;
                    }
                }
            }
        }
        finally {
            log.info("Shutting down indexing thread pool");
            executor.shutdownNow();
        }
    }

    @VisibleForTesting
    protected List<Future<?>> submitBatches(ExecutorService executor, @Nullable List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle> handles, ReindexProgress progress) {
        if (handles == null || handles.isEmpty()) {
            return Collections.emptyList();
        }
        int numberToPop = Integer.getInteger("confluence.reindex.documents.to.pop", 20);
        Map<String, List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle>> hibernateHandlesGroupedByType = handles.stream().collect(Collectors.groupingBy(HibernateHandle::getClassName));
        if (log.isInfoEnabled()) {
            String countSummary = hibernateHandlesGroupedByType.entrySet().stream().map(entry -> String.format("%d %s", ((List)entry.getValue()).size(), entry.getKey())).collect(Collectors.joining(", "));
            log.info("Partitioning indexable entities [{}] up to {} at a time across indexing threads", (Object)countSummary, (Object)numberToPop);
        }
        return hibernateHandlesGroupedByType.values().stream().flatMap(hibernateHandles -> Lists.partition((List)hibernateHandles, (int)numberToPop).stream()).map(hibernateHandleBatch -> executor.submit(ThreadLocalTenantGate.withTenantPermit(Executors.callable(() -> {
            try {
                progress.reindexBatchStarted();
                this.delegate.index((List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle>)hibernateHandleBatch, progress);
            }
            catch (Exception e) {
                log.error("An error occurred while re-indexing a batch. Only the particular batch which had an error occur will not be re-indexed correctly.", (Throwable)e);
            }
            finally {
                this.updateProgress(progress, hibernateHandleBatch.size());
                progress.reindexBatchFinished();
            }
        })))).collect(Collectors.toList());
    }

    private synchronized void updateProgress(ReindexProgress progress, int delta) {
        progress.increment(delta);
        ConcurrentBatchIndexer.logProgress(progress);
        log.debug("BatchIndexer batch complete");
    }

    private static void logProgress(Progress progress) {
        int percentComplete = progress.getPercentComplete();
        if (percentComplete < 100) {
            log.info("Re-index progress: {} of {}. {}% complete. Memory usage: {}", new Object[]{progress.getCount(), progress.getTotal(), percentComplete, ConcurrentBatchIndexer.getMemoryUsageSummary()});
        } else {
            log.info("Re-index progress: {}% complete. {} items have been reindexed", (Object)percentComplete, (Object)progress.getCount());
        }
    }

    private static String getMemoryUsageSummary() {
        return String.format("%s free, %s total", FileUtils.byteCountToDisplaySize((long)Runtime.getRuntime().freeMemory()), FileUtils.byteCountToDisplaySize((long)Runtime.getRuntime().totalMemory()));
    }
}

