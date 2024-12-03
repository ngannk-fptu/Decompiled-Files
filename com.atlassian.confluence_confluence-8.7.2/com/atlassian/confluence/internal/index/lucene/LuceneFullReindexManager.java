/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.util.concurrent.atomic.AtomicInteger
 *  com.atlassian.util.profiling.LongRunningMetricTimer
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  net.jcip.annotations.ThreadSafe
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.cluster.ReIndexingScopeThreadLocal;
import com.atlassian.confluence.core.persistence.SearchableDao;
import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.confluence.internal.index.IndexLockService;
import com.atlassian.confluence.internal.index.ReIndexer;
import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.util.concurrent.atomic.AtomicInteger;
import com.atlassian.util.profiling.LongRunningMetricTimer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
@LuceneIndependent
public class LuceneFullReindexManager
implements FullReindexManager {
    private static final Logger log = LoggerFactory.getLogger(LuceneFullReindexManager.class);
    private static final Integer LOCK_TIMEOUT_MINUTES = Integer.getInteger("confluence.index.manager.lock.timeout");
    public static final String REINDEX_METRIC_NAME = "index.reindex";
    private static final String INTERACTIVE_REINDEXING_THREAD_NAME = "lucene-interactive-reindexing-thread";
    private static final EnumSet<SearchIndex> indexesToLock = EnumSet.allOf(SearchIndex.class);
    public static final long STOP_TASK_POLLING_INTERVAL = 100L;
    private final Collection<SearchIndexAccessor> searchIndexAccessors;
    private final IndexLockService lockService;
    private final ReIndexer reIndexer;
    private final SearchableDao searchableDao;
    private final DarkFeatureManager darkFeatureManager;
    private final AtomicInteger nextJobId = new AtomicInteger(1);
    private volatile ReIndexTask lastReindexTask;

    public LuceneFullReindexManager(IndexLockService lockService, ReIndexer reIndexer, SearchableDao searchableDao, Collection<SearchIndexAccessor> searchIndexAccessors, DarkFeatureManager darkFeatureManager) {
        this.lockService = lockService;
        this.reIndexer = reIndexer;
        this.searchableDao = searchableDao;
        this.searchIndexAccessors = searchIndexAccessors;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public ReIndexTask reIndex() {
        return this.reIndex(ReIndexOption.fullReindex());
    }

    @Override
    public ReIndexTask reIndex(EnumSet<ReIndexOption> options) {
        return this.doReIndex(options, null, Collections.emptyList());
    }

    @Override
    public ReIndexTask reIndex(EnumSet<ReIndexOption> options, SearchQuery searchQuery) {
        return this.doReIndex(options, searchQuery, Collections.emptyList());
    }

    @Override
    public ReIndexTask reIndex(EnumSet<ReIndexOption> options, @NonNull List<String> spaceKeys) {
        if (!spaceKeys.isEmpty() && !this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.spaces").orElse(false).booleanValue()) {
            throw new UnsupportedOperationException();
        }
        return this.doReIndex(options, null, spaceKeys);
    }

    private ReIndexTask doReIndex(EnumSet<ReIndexOption> options, SearchQuery searchQuery, List<String> spaceKeys) {
        boolean gotLocalLocks = false;
        try {
            gotLocalLocks = this.fullReindexLock();
            if (gotLocalLocks) {
                if (this.isReIndexing()) {
                    log.warn("A reindex was requested but there is already one in progress. Ignoring request.");
                    ReIndexTask reIndexTask = this.getLastReindexingTask();
                    return reIndexTask;
                }
                LongRunningMetricTimer timer = Metrics.metric((String)REINDEX_METRIC_NAME).tag("indexAttachments", options.contains((Object)ReIndexOption.ATTACHMENT_ONLY)).tag("indexContent", options.contains((Object)ReIndexOption.CONTENT_ONLY)).tag("indexUsers", options.contains((Object)ReIndexOption.USER_ONLY)).tag("limitedWithQuery", searchQuery != null).withInvokerPluginKey().withAnalytics().longRunningTimer();
                this.lastReindexTask = new ReIndexTask(this.reIndexer, this.searchableDao, spaceKeys, options, Optional.ofNullable(searchQuery), this.nextJobId.getAndIncrement());
                ReIndexingScopeThreadLocal.ReIndexingScope currentScope = ReIndexingScopeThreadLocal.currentScope();
                ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
                new Thread(() -> {
                    AuthenticatedUserThreadLocal.set(currentUser);
                    try (Ticker ignored = timer.start();){
                        AuthenticatedUserThreadLocal.set(currentUser);
                        ThreadLocalTenantGate.withTenantPermit(Executors.callable(() -> ReIndexingScopeThreadLocal.withScope(currentScope, this.lastReindexTask))).call();
                    }
                    catch (Exception e) {
                        throw new RuntimeException("There has been a problem during full reindex", e);
                    }
                }, INTERACTIVE_REINDEXING_THREAD_NAME).start();
                ReIndexTask reIndexTask = this.lastReindexTask;
                return reIndexTask;
            }
            throw new RuntimeException("Timed out waiting to acquire the lock for full reindexing");
        }
        finally {
            if (gotLocalLocks) {
                this.fullReindexUnlock();
            }
        }
    }

    @Override
    public ReIndexTask getLastReindexingTask() {
        return this.lastReindexTask;
    }

    @Override
    public boolean isReIndexing() {
        ReIndexTask task = this.lastReindexTask;
        return task != null && !task.isFinishedReindexing();
    }

    @Override
    public void unIndexAll() {
        boolean gotLock = this.lockService.tryLock(indexesToLock, 10L, TimeUnit.SECONDS);
        if (!gotLock) {
            log.error("Unable to acquire the lock to the connection");
            return;
        }
        try {
            if (this.isReIndexing()) {
                log.error("Full reindex is in progress. Will not continue.");
                return;
            }
            this.searchIndexAccessors.forEach(indexAccessor -> indexAccessor.execute(SearchIndexWriter::deleteAll));
        }
        finally {
            this.lockService.unlock(indexesToLock);
        }
    }

    private boolean fullReindexLock() {
        log.info("Locking indexes for full reindex");
        if (LOCK_TIMEOUT_MINUTES != null) {
            return this.lockService.tryLock(indexesToLock, (long)LOCK_TIMEOUT_MINUTES.intValue(), TimeUnit.MINUTES);
        }
        this.lockService.lock(indexesToLock);
        return true;
    }

    private void fullReindexUnlock() {
        log.info("Unlocking indexes after full reindex");
        this.lockService.unlock(indexesToLock);
    }
}

