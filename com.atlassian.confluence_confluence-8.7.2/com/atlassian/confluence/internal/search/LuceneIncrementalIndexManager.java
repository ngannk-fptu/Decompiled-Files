/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.atomic.AtomicInteger
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.event.events.search.IndexQueueFlushCompleteEvent;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.atlassian.confluence.internal.index.IndexLockService;
import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@LuceneIndependent
@Internal
public class LuceneIncrementalIndexManager
implements IncrementalIndexManager {
    public static final String QUEUE_BATCH_SIZE_KEY = "index.queue.batch.size";
    @VisibleForTesting
    static final int DEFAULT_QUEUE_BATCH_SIZE = 1500;
    private final Integer QUEUE_BATCH_SIZE = Integer.getInteger("index.queue.batch.size", 1500);
    @VisibleForTesting
    static final int CLEAR_SESSION_BATCH_SIZE = 100;
    private static final Integer LOCK_TIMEOUT_MINUTES = Integer.getInteger("confluence.index.manager.lock.timeout");
    private static final Logger log = LoggerFactory.getLogger(LuceneIncrementalIndexManager.class);
    private final IndexLockService lockService;
    private final IndexTaskQueue<ConfluenceIndexTask> taskQueue;
    private final SearchIndexAccessor searchIndexAccessor;
    private final EventPublisher eventPublisher;
    private final FullReindexManager fullReindexManager;
    private final SearchIndex targetIndex;
    private final SessionFactory sessionFactory;
    private final HibernateSessionManager5 sessionManager;
    private final JournalService journalService;
    private volatile boolean flushing = false;
    private volatile FlushStatistics stats;

    public LuceneIncrementalIndexManager(IndexLockService lockService, IndexTaskQueue<ConfluenceIndexTask> taskQueue, SearchIndexAccessor searchIndexAccessor, EventPublisher eventPublisher, FullReindexManager fullReindexManager, SearchIndex targetIndex, SessionFactory sessionFactory, HibernateSessionManager5 sessionManager, JournalService journalService) {
        this.lockService = lockService;
        this.taskQueue = taskQueue;
        this.searchIndexAccessor = searchIndexAccessor;
        this.eventPublisher = eventPublisher;
        this.fullReindexManager = fullReindexManager;
        this.targetIndex = targetIndex;
        this.sessionFactory = sessionFactory;
        this.sessionManager = sessionManager;
        this.journalService = journalService;
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public boolean isFlushing() {
        return this.flushing;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public boolean flushQueue(IndexManager.IndexQueueFlushMode flushMode) {
        log.debug("Flush requested");
        boolean gotLock = false;
        AtomicInteger processedTaskCount = new AtomicInteger();
        try {
            gotLock = this.tryLockSingleIndex();
            if (gotLock) {
                if (this.fullReindexManager.isReIndexing()) {
                    log.debug("Reindex is ongoing, flush is skipped");
                    boolean bl = false;
                    return bl;
                }
                this.flushing = true;
                try {
                    FlushStatistics currentFlushStats = new FlushStatistics();
                    currentFlushStats.setStarted(new Date());
                    this.searchIndexAccessor.execute(writer -> {
                        Consumer<ConfluenceIndexTask> exceptionCapturingTask = task -> {
                            try {
                                task.perform(writer);
                            }
                            catch (IOException e) {
                                throw new SearchIndexAccessException("Unexpected IOException while performing task", e);
                            }
                        };
                        this.flushAndExecuteInBatches(flushMode, exceptionCapturingTask, processedTaskCount);
                    });
                    currentFlushStats.setQueueSize(processedTaskCount.get());
                    if (processedTaskCount.get() > 0) {
                        currentFlushStats.setRecreated(false);
                        currentFlushStats.setFinished(new Date());
                        this.stats = currentFlushStats;
                        this.eventPublisher.publish((Object)new IndexQueueFlushCompleteEvent(this, currentFlushStats));
                        log.debug("Flushed {} items in {} milliseconds", (Object)this.stats.getQueueSize(), (Object)this.stats.getElapsedMilliseconds());
                    } else {
                        log.debug("There were no tasks on the index queue");
                    }
                    boolean bl = true;
                    return bl;
                }
                finally {
                    this.flushing = false;
                }
            }
            throw new RuntimeException("Timed out waiting for lock for flushing");
        }
        finally {
            if (gotLock) {
                this.unlockSingleIndex();
            }
        }
    }

    @VisibleForTesting
    void flushAndExecuteInBatches(IndexManager.IndexQueueFlushMode flushMode, Consumer<ConfluenceIndexTask> exceptionCapturingTask, AtomicInteger processedTaskCount) {
        try (Cleanup ignore = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            int totalToProcess;
            int queueSize = this.taskQueue.getSize();
            if (flushMode == IndexManager.IndexQueueFlushMode.ENTIRE_QUEUE) {
                totalToProcess = queueSize;
                try {
                    this.journalService.waitForRecentEntriesToBecomeVisible();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                totalToProcess = Math.min(this.QUEUE_BATCH_SIZE, queueSize);
            }
            this.sessionFactory.getCurrentSession().flush();
            int totalFlushed = this.sessionManager.executeThenClearSessionWithoutCommitOrFlush(100, totalToProcess, batchSize -> this.taskQueue.flushAndExecute(exceptionCapturingTask, (int)batchSize));
            processedTaskCount.set(totalFlushed);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void resetIndexQueue() {
        log.warn("Resetting index task queue");
        this.taskQueue.reset();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public FlushStatistics getLastNonEmptyFlushStats() {
        return this.stats;
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public int getQueueSize() {
        return this.taskQueue.getSize();
    }

    private boolean tryLockSingleIndex() {
        if (LOCK_TIMEOUT_MINUTES != null) {
            return this.lockService.tryLock(this.targetIndex, (long)LOCK_TIMEOUT_MINUTES.intValue(), TimeUnit.MINUTES);
        }
        this.lockService.lock(this.targetIndex);
        return true;
    }

    private void unlockSingleIndex() {
        this.lockService.unlock(this.targetIndex);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addTask(ConfluenceIndexTask task) {
        this.taskQueue.enqueue(task);
    }

    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public SearchIndex getTargetIndex() {
        return this.targetIndex;
    }
}

