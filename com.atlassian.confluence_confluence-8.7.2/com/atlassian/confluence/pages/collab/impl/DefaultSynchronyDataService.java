/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.SynchronyRowsCount
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.eviction.SynchronyDataService
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.collab.impl;

import com.atlassian.confluence.api.model.SynchronyRowsCount;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.eviction.SynchronyDataService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.collab.SynchronyLockManager;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgress;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgressTracking;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionSearchType;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionType;
import com.atlassian.confluence.pages.persistence.dao.SynchronyEvictionDao;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSynchronyDataService
implements SynchronyDataService {
    private static final Logger log = LoggerFactory.getLogger(DefaultSynchronyDataService.class);
    private static final int CONTENT_REMOVAL_BATCH_PER_TX = Integer.getInteger("synchrony.eviction.removal.content.batch.count.per.tx", 10);
    private static final int CONTENT_SOFT_SEARCH_BATCH_PER_TX = Integer.getInteger("synchrony.eviction.content.soft.search.count.per.tx", 200);
    private static final int CONTENT_HARD_SEARCH_BATCH_PER_TX = Integer.getInteger("synchrony.eviction.content.hard.search.count.per.tx", 100000);
    private static final long DEFAULT_LOCK_TIMEOUT = Long.getLong("synchrony.eviction.content.lock.timeout.default.ms", 300000L);
    private final SynchronyLockManager lockManager;
    private final SynchronyEvictionDao evictionDao;
    private final SynchronyEvictionProgressTracking progressTracking;
    private final PageManager pageManager;

    public DefaultSynchronyDataService(SynchronyLockManager lockManager, SynchronyEvictionDao evictionDao, SynchronyEvictionProgressTracking progressTracking, PageManager pageManager) {
        this.lockManager = lockManager;
        this.evictionDao = evictionDao;
        this.progressTracking = progressTracking;
        this.pageManager = pageManager;
    }

    public SynchronyRowsCount currentSynchronyDatasetSize(Long contentId) {
        return new SynchronyRowsCount(this.evictionDao.getEventsCount(contentId), this.evictionDao.getSnapshotsCount(contentId));
    }

    public void softRemoveHistoryOlderThan(int thresholdHours, int contentCount) {
        SynchronyEvictionProgress progress = this.progressTracking.startEviction(SynchronyEvictionType.SOFT, thresholdHours, contentCount);
        try {
            RemovalProgress totalProgress = RemovalProgress.zero();
            int searchBatch = Math.min(contentCount, CONTENT_SOFT_SEARCH_BATCH_PER_TX);
            int searchIterations = (contentCount + searchBatch - 1) / searchBatch;
            for (int searchIteration = 0; searchIteration < searchIterations; ++searchIteration) {
                RemovalProgress iterationProgress = this.removeContentHistoryInBatches(progress, SynchronyEvictionSearchType.SOFT, () -> this.evictionDao.findSafeContentWithHistoryOlderThan(thresholdHours, thresholdHours, searchBatch), searchBatch);
                totalProgress = totalProgress.mergeWith(iterationProgress);
                if (iterationProgress.finished) break;
            }
            this.progressTracking.finishEviction(progress, totalProgress.contentsRemoved, totalProgress.rowsRemoved);
        }
        catch (RuntimeException e) {
            this.progressTracking.failEviction(progress);
            throw e;
        }
    }

    public void hardRemoveHistoryOlderThan(int thresholdHours) {
        SynchronyEvictionProgress progress = this.progressTracking.startEviction(SynchronyEvictionType.HARD, thresholdHours);
        try {
            RemovalProgress iterationProgress;
            RemovalProgress totalProgress = RemovalProgress.zero();
            do {
                iterationProgress = this.removeContentHistoryInBatches(progress, SynchronyEvictionSearchType.HARD_SNAPSHOTS, () -> this.evictionDao.findContentWithAnySnapshotOlderThan(thresholdHours, CONTENT_HARD_SEARCH_BATCH_PER_TX), CONTENT_HARD_SEARCH_BATCH_PER_TX);
                totalProgress = totalProgress.mergeWith(iterationProgress);
            } while (!iterationProgress.finished);
            do {
                iterationProgress = this.removeContentHistoryInBatches(progress, SynchronyEvictionSearchType.HARD_EVENTS, () -> this.evictionDao.findContentWithAnyEventOlderThan(thresholdHours, CONTENT_SOFT_SEARCH_BATCH_PER_TX), CONTENT_HARD_SEARCH_BATCH_PER_TX);
                totalProgress = totalProgress.mergeWith(iterationProgress);
            } while (!iterationProgress.finished);
            this.progressTracking.finishEviction(progress, totalProgress.contentsRemoved, totalProgress.rowsRemoved);
        }
        catch (RuntimeException e) {
            this.progressTracking.failEviction(progress);
            throw e;
        }
    }

    public void removeHistoryFor(ContentId contentId) {
        Objects.requireNonNull(contentId);
        AbstractPage page = this.pageManager.getAbstractPage(contentId.asLong());
        if (!(page instanceof Page) && !(page instanceof BlogPost)) {
            throw new IllegalArgumentException("Content should be either Page or a BlogPost");
        }
        SynchronyEvictionProgress progress = this.progressTracking.startEviction(SynchronyEvictionType.INDIVIDUAL, 0);
        try {
            int rowsRemoved = this.removeSynchronyDataFor(progress, Collections.singletonList(contentId.asLong()));
            this.progressTracking.finishEviction(progress, 1, rowsRemoved);
        }
        catch (RuntimeException e) {
            this.progressTracking.failEviction(progress);
            throw e;
        }
    }

    public void removeApplicationCredentials(String applicationId) {
        this.removeApplicationCredentialsImpl(applicationId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeApplicationCredentialsImpl(String applicationId) {
        SynchronyLockManager.SynchronyContentLock lock = null;
        try {
            lock = (SynchronyLockManager.SynchronyContentLock)this.lockManager.lockAllContent(DEFAULT_LOCK_TIMEOUT);
        }
        catch (Exception e) {
            log.error("Error locking content in Synchrony: {}", (Object)e.toString());
            log.debug("Error locking content in Synchrony", (Throwable)e);
        }
        try {
            this.evictionDao.removeApplicationIds(Collections.singletonList(Objects.requireNonNull(applicationId)));
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void dataCleanUpAfterTurningOffCollabEditing(String appId) {
        try {
            this.evictionDao.removeContentProperties();
        }
        catch (Exception e) {
            log.error("An error occurred when removing the 'sync-rev' and 'sync-rev-source' content properties", (Object)GeneralUtil.getStackTrace(e));
        }
        try {
            this.removeApplicationCredentialsImpl(appId);
            this.removeApplicationCredentialsImpl(appId + "-debug");
        }
        catch (Exception e) {
            log.error("Error deleting application data from Synchrony tables");
        }
    }

    private RemovalProgress removeContentHistoryInBatches(SynchronyEvictionProgress progress, SynchronyEvictionSearchType type, Supplier<List<Long>> searchFunction, Integer limit) {
        List<Long> contentIds;
        this.progressTracking.startSearch(progress, type, limit);
        try {
            contentIds = searchFunction.get();
            this.progressTracking.finishSearch(progress, contentIds.size());
        }
        catch (RuntimeException e) {
            this.progressTracking.failSearch(progress);
            throw e;
        }
        if (contentIds.isEmpty()) {
            return RemovalProgress.nothingFound();
        }
        int totalContentRemoved = 0;
        int totalRowsRemoved = 0;
        List removalBatches = Lists.partition(contentIds, (int)CONTENT_REMOVAL_BATCH_PER_TX);
        for (List removalBatch : removalBatches) {
            int entitiesRemoved = removalBatch.size();
            int rowsRemoved = this.removeSynchronyDataFor(progress, removalBatch);
            totalContentRemoved += entitiesRemoved;
            totalRowsRemoved += rowsRemoved;
        }
        return RemovalProgress.removed(totalContentRemoved, totalRowsRemoved);
    }

    private int removeSynchronyDataFor(SynchronyEvictionProgress progress, Collection<Long> contentIds) {
        int n;
        block8: {
            this.progressTracking.startRemovalUnderLock(progress, contentIds.size());
            Object ignored = this.lockManager.lockContent(contentIds, DEFAULT_LOCK_TIMEOUT);
            try {
                int rowsRemoved = this.evictionDao.removeAllSynchronyDataFor(contentIds);
                this.progressTracking.finishRemovalUnderLock(progress, rowsRemoved);
                n = rowsRemoved;
                if (ignored == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    this.progressTracking.failRemovalUnderLock(progress);
                    throw new RuntimeException(e);
                }
            }
            ignored.close();
        }
        return n;
    }

    private static class RemovalProgress {
        final int contentsRemoved;
        final int rowsRemoved;
        final boolean finished;

        static RemovalProgress nothingFound() {
            return new RemovalProgress(0, 0, true);
        }

        static RemovalProgress removed(int contentsRemoved, int rowsRemoved) {
            return new RemovalProgress(contentsRemoved, rowsRemoved, false);
        }

        static RemovalProgress zero() {
            return new RemovalProgress(0, 0, false);
        }

        RemovalProgress(int contentsRemoved, int rowsRemoved, boolean finished) {
            this.contentsRemoved = contentsRemoved;
            this.rowsRemoved = rowsRemoved;
            this.finished = finished;
        }

        RemovalProgress mergeWith(RemovalProgress other) {
            return new RemovalProgress(this.contentsRemoved + other.contentsRemoved, this.rowsRemoved + other.rowsRemoved, this.finished || other.finished);
        }
    }
}

