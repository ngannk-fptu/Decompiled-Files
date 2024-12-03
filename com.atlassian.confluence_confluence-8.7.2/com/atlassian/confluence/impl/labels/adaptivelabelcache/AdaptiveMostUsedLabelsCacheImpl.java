/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 */
package com.atlassian.confluence.impl.labels.adaptivelabelcache;

import com.atlassian.confluence.impl.labels.adaptivelabelcache.AdaptiveMostUsedLabelsCache;
import com.atlassian.confluence.impl.labels.adaptivelabelcache.LiteSearchResultCacheEntry;
import com.atlassian.confluence.impl.labels.adaptivelabelcache.dao.AdaptiveLabelCacheDao;
import com.atlassian.confluence.internal.labels.LabelManagerInternal;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class AdaptiveMostUsedLabelsCacheImpl
implements AdaptiveMostUsedLabelsCache {
    private static final Logger log = LoggerFactory.getLogger(AdaptiveMostUsedLabelsCacheImpl.class);
    private static final long SITE_ID = 0L;
    private static final String MAX_MOST_USED_LABELS_LIMIT_PARAMETER_NAME = "confluence.labels.most-used.max-limit";
    private static final int MAX_MOST_USED_LABELS_LIMIT = Integer.getInteger("confluence.labels.most-used.max-limit", 1000);
    private final AtomicLong oldRecordsLastCleanUpTime = new AtomicLong();
    private static final long OLD_RECORDS_CLEAN_UP_PERIOD_MS = Long.getLong("confluence.labels.most-used.old-record-cleanup-period-ms", 39600000L);
    private static final long OLD_RECORDS_TIME_TO_LIVE_DAYS = Long.getLong("confluence.labels.most-used.old-record-time-to-live-days", 60L);
    private static final boolean RETURN_LESS_POPULAR_LABELS_IF_POSSIBLE = Boolean.getBoolean("confluence.labels.most-used.return-less-records-if-possible");
    private static final String BASE_EXPIRATION_TIME_PARAMETER_NAME = "confluence.labels.most-used.base-expiration-time-sec";
    private static final int BASE_EXPIRATION_TIME_SEC = Integer.getInteger("confluence.labels.most-used.base-expiration-time-sec", 60);
    private final AdaptiveLabelCacheDao adaptiveLabelCacheDao;
    private final LabelManagerInternal labelManagerDelegate;
    private final TimestampProvider timestampProvider;
    private final Map<Long, SpaceRefreshTask> tasksQueue = Collections.synchronizedMap(new LinkedHashMap());

    @VisibleForTesting
    public AdaptiveMostUsedLabelsCacheImpl(TimestampProvider timestampProvider, AdaptiveLabelCacheDao adaptiveLabelCacheDao, LabelManagerInternal labelManagerDelegate) {
        this.adaptiveLabelCacheDao = adaptiveLabelCacheDao;
        this.labelManagerDelegate = labelManagerDelegate;
        this.timestampProvider = timestampProvider;
        this.oldRecordsLastCleanUpTime.set(timestampProvider.getCurrentTimeMillis());
    }

    public AdaptiveMostUsedLabelsCacheImpl(AdaptiveLabelCacheDao adaptiveLabelCacheDao, LabelManagerInternal labelManagerDelegate) {
        this(new TimestampProvider(), adaptiveLabelCacheDao, labelManagerDelegate);
    }

    @Override
    public List<LiteLabelSearchResult> getSpaceRecord(String spaceKey, int limit) {
        long spaceId = this.adaptiveLabelCacheDao.getSpaceIdByKey(spaceKey);
        return this.getSpaceRecord(spaceId, limit);
    }

    @Override
    public void deleteAllPersistedRecords() {
        this.adaptiveLabelCacheDao.clear();
    }

    @Override
    public void deletePersistedRecord(long spaceId) {
        this.adaptiveLabelCacheDao.removeRecord(spaceId);
    }

    @Override
    public void deletePersistedRecordForSite() {
        this.deletePersistedRecord(0L);
    }

    @Scheduled(fixedDelay=3000L)
    public void scheduled() {
        Iterator<Map.Entry<Long, SpaceRefreshTask>> iterator = this.tasksQueue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SpaceRefreshTask> entry = iterator.next();
            iterator.remove();
            this.retrieveDataFromDelegateAndRefreshCacheEntry(entry.getValue(), true);
        }
        this.deleteOldElementsIfRequired();
    }

    private void deleteOldElementsIfRequired() {
        long timestamp = this.timestampProvider.getCurrentTimeMillis();
        if (this.oldRecordsLastCleanUpTime.get() + OLD_RECORDS_CLEAN_UP_PERIOD_MS > timestamp) {
            return;
        }
        this.oldRecordsLastCleanUpTime.set(timestamp);
        this.adaptiveLabelCacheDao.removeRecordsExpiredAfter(timestamp - OLD_RECORDS_TIME_TO_LIVE_DAYS * 24L * 3600000L);
    }

    private LiteSearchResultCacheEntry retrieveDataFromDelegateAndRefreshCacheEntry(SpaceRefreshTask spaceRefreshTask, boolean checkExistingCacheRecord) {
        long startExecutionTime = this.timestampProvider.getCurrentTimeMillis();
        if (checkExistingCacheRecord) {
            long requestTime = spaceRefreshTask.getTimestamp();
            LiteSearchResultCacheEntry entry = this.adaptiveLabelCacheDao.read(spaceRefreshTask.getSpaceId());
            if (entry != null && entry.getExpirationTs() > requestTime) {
                return entry;
            }
        }
        List<LiteLabelSearchResult> foundLabels = this.retrieveDataFromDelegate(spaceRefreshTask.getSpaceId(), spaceRefreshTask.getLimit());
        return this.updateRecordInPersistentCache(spaceRefreshTask.getSpaceId(), foundLabels, spaceRefreshTask.getLimit(), startExecutionTime);
    }

    private List<LiteLabelSearchResult> retrieveDataFromDelegate(long spaceId, int limit) {
        if (spaceId == 0L) {
            return this.labelManagerDelegate.getMostPopularLabelsInSiteLite(limit);
        }
        String spaceKey = this.adaptiveLabelCacheDao.getSpaceKeyFromSpaceId(spaceId);
        return this.labelManagerDelegate.getMostPopularLabelsInSpaceLite(spaceKey, limit);
    }

    @Override
    public List<LiteLabelSearchResult> getSiteRecord(int limit) {
        return this.getSpaceRecord(0L, limit);
    }

    @Override
    public List<LiteLabelSearchResult> getSpaceRecord(long spaceId, int limit) {
        if ((limit = this.calculateEffectiveLimit(limit)) == 0) {
            return Collections.emptyList();
        }
        int limitForCache = this.nextPowerOf2(limit);
        List<LiteLabelSearchResult> persistentCacheResults = this.retrieveDataFromPersistentCache(spaceId, limit, limitForCache);
        if (persistentCacheResults != null) {
            return persistentCacheResults;
        }
        log.debug("Record was not found in the persistent cache for the space with id {}. A request will be be sent to DB", (Object)spaceId);
        SpaceRefreshTask spaceRefreshTask = new SpaceRefreshTask(spaceId, limitForCache, this.timestampProvider.getCurrentTimeMillis());
        return this.retrieveDataFromDelegateAndRefreshCacheEntry(spaceRefreshTask, false).getList(limit);
    }

    private List<LiteLabelSearchResult> retrieveDataFromPersistentCache(long spaceId, int limit, int limitForCache) {
        LiteSearchResultCacheEntry persistentCacheSearchResult = this.adaptiveLabelCacheDao.read(spaceId);
        if (persistentCacheSearchResult != null && (RETURN_LESS_POPULAR_LABELS_IF_POSSIBLE || persistentCacheSearchResult.hasEnoughRecordsForTheNewLimit(limit))) {
            long expiredInTs = persistentCacheSearchResult.getExpirationTs() - this.timestampProvider.getCurrentTimeMillis();
            if (expiredInTs <= 0L) {
                log.debug("Found a stale record in the persistent cache for the space with id {}. It expired {} sec ago", (Object)spaceId, (Object)(-expiredInTs / 1000L));
                this.addTaskToRefreshPersistentCache(spaceId, Math.max(limitForCache, persistentCacheSearchResult.getRequestedLimit()));
            } else {
                log.debug("Found a record in persistent cache. Will expire in {} sec", (Object)(expiredInTs / 1000L));
            }
            return persistentCacheSearchResult.getList(limit);
        }
        return null;
    }

    @VisibleForTesting
    public synchronized void addTaskToRefreshPersistentCache(long spaceId, int requestedLimit) {
        this.tasksQueue.compute(spaceId, (key, prevValue) -> {
            if (prevValue == null) {
                return new SpaceRefreshTask(spaceId, requestedLimit, this.timestampProvider.getCurrentTimeMillis());
            }
            return new SpaceRefreshTask(spaceId, Math.max(prevValue.getLimit(), requestedLimit), Math.max(prevValue.getTimestamp(), this.timestampProvider.getCurrentTimeMillis()));
        });
    }

    private int calculateEffectiveLimit(int limit) {
        if (limit == -1 || limit == 0) {
            return MAX_MOST_USED_LABELS_LIMIT;
        }
        if (limit < 0) {
            throw new IllegalArgumentException("Limit can't be negative. Requested value is " + limit);
        }
        if (limit > MAX_MOST_USED_LABELS_LIMIT) {
            log.warn("Requested limit ({}) must not exceed the max limit ({}). To increase the max limit, adjust '{}' system variable", new Object[]{limit, MAX_MOST_USED_LABELS_LIMIT, MAX_MOST_USED_LABELS_LIMIT_PARAMETER_NAME});
            return MAX_MOST_USED_LABELS_LIMIT;
        }
        return limit;
    }

    private int nextPowerOf2(int value) {
        return value == 1 ? 1 : Integer.highestOneBit(value - 1) * 2;
    }

    private LiteSearchResultCacheEntry updateRecordInPersistentCache(long spaceId, List<LiteLabelSearchResult> labels, int requestedLimit, long requestTime) {
        long expirationTime = this.timestampProvider.getCurrentTimeMillis() + this.calculateExpirationTimeoutForPersistentCache(labels);
        log.debug("Updating persistent cache. Space {}, labels size {}, limit {}", new Object[]{spaceId, labels.size(), requestedLimit});
        LiteSearchResultCacheEntry cacheEntry = new LiteSearchResultCacheEntry(labels, requestedLimit, expirationTime, requestTime);
        this.adaptiveLabelCacheDao.write(spaceId, cacheEntry);
        return cacheEntry;
    }

    private long calculateExpirationTimeoutForPersistentCache(List<LiteLabelSearchResult> list) {
        int MIN_RESULT_SIZE = 20;
        long DEFAULT_EXPIRATION_TIME_FOR_SMALL_SPACES = 60000L;
        if (list.size() < 20) {
            return 60000L;
        }
        int minCountInTheTop = list.subList(0, 20).stream().mapToInt(LiteLabelSearchResult::getCount).min().getAsInt();
        return minCountInTheTop * BASE_EXPIRATION_TIME_SEC * 1000;
    }

    public int getTaskQueueSize() {
        return this.tasksQueue.size();
    }

    private static class SpaceRefreshTask {
        private final long spaceId;
        private final long timestamp;
        private final int limit;

        public SpaceRefreshTask(long spaceId, int limit, long timestamp) {
            this.spaceId = spaceId;
            this.limit = limit;
            this.timestamp = timestamp;
        }

        public long getSpaceId() {
            return this.spaceId;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public int getLimit() {
            return this.limit;
        }
    }

    @VisibleForTesting
    public static class TimestampProvider {
        public long getCurrentTimeMillis() {
            return System.currentTimeMillis();
        }
    }
}

