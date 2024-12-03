/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

public interface LegacyCacheStatistics {
    public long getCacheHitCount();

    public long getInMemoryHitCount();

    public long getOffHeapHitCount();

    public long getOnDiskHitCount();

    public long getCacheMissCount();

    public long getInMemoryMissCount();

    public long getOffHeapMissCount();

    public long getOnDiskMissCount();

    public long getCacheMissCountExpired();

    public int getCacheHitRatio();

    public int getNonstopTimeoutRatio();

    public long getSize();

    @Deprecated
    public long getInMemorySize();

    @Deprecated
    public long getOffHeapSize();

    @Deprecated
    public long getOnDiskSize();

    public long getLocalHeapSize();

    public long getLocalOffHeapSize();

    public long getLocalDiskSize();

    public long getLocalHeapSizeInBytes();

    public long getLocalOffHeapSizeInBytes();

    public long getLocalDiskSizeInBytes();

    public long getAverageGetTimeNanos();

    public long getEvictedCount();

    public long getPutCount();

    public long getUpdateCount();

    public long getReplaceOneArgSuccessCount();

    public long getReplaceOneArgSuccessRate();

    public long getReplaceOneArgMissCount();

    public long getReplaceOneArgMissRate();

    public long getReplaceTwoArgSuccessCount();

    public long getReplaceTwoArgSuccessRate();

    public long getReplaceTwoArgMissCount();

    public long getReplaceTwoArgMissRate();

    public long getPutIfAbsentSuccessCount();

    public long getPutIfAbsentSuccessRate();

    public long getPutIfAbsentMissCount();

    public long getPutIfAbsentMissRate();

    public long getRemoveElementSuccessCount();

    public long getRemoveElementSuccessRate();

    public long getRemoveElementMissCount();

    public long getRemoveElementMissRate();

    public long getExpiredCount();

    public long getRemovedCount();

    public long getCacheClusterOfflineCount();

    public long getCacheClusterRejoinCount();

    public long getCacheClusterOnlineCount();

    public String getCacheName();

    public Long getMaxGetTimeNanos();

    public Long getMinGetTimeNanos();

    public long getWriterQueueLength();

    public long getXaCommitCount();

    public long getXaRollbackCount();

    public long getXaRecoveredCount();

    public long getCacheHitMostRecentSample();

    public long getCacheHitInMemoryMostRecentSample();

    public long getCacheHitOffHeapMostRecentSample();

    public long getCacheHitOnDiskMostRecentSample();

    public long getCacheMissMostRecentSample();

    public long getCacheMissInMemoryMostRecentSample();

    public long getCacheMissOffHeapMostRecentSample();

    public long getCacheMissOnDiskMostRecentSample();

    public long getCacheMissExpiredMostRecentSample();

    public long getCacheMissNotFoundMostRecentSample();

    public int getCacheHitRatioMostRecentSample();

    public long getCacheElementEvictedMostRecentSample();

    public long getCacheElementRemovedMostRecentSample();

    public long getCacheElementExpiredMostRecentSample();

    public long getCacheElementPutMostRecentSample();

    public long getCacheElementUpdatedMostRecentSample();

    public long getAverageGetTimeNanosMostRecentSample();

    public void dispose();

    public long getAverageSearchTimeNanos();

    public long getSearchesPerSecond();

    public long getCacheXaCommitsMostRecentSample();

    public long getCacheXaRollbacksMostRecentSample();

    public boolean isLocalHeapCountBased();

    public long getCacheClusterOfflineMostRecentSample();

    public long getCacheClusterRejoinMostRecentSample();

    public long getCacheClusterOnlineMostRecentSample();

    public long getNonStopSuccessCount();

    public long getNonStopFailureCount();

    public long getNonStopRejoinTimeoutCount();

    public long getNonStopTimeoutCount();

    public long getNonStopSuccessMostRecentSample();

    public long getNonStopFailureMostRecentSample();

    public long getNonStopRejoinTimeoutMostRecentSample();

    public long getNonStopTimeoutMostRecentSample();
}

