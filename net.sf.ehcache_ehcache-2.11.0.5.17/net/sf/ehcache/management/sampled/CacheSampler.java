/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import java.util.Map;
import net.sf.ehcache.management.sampled.LegacyCacheStatistics;
import net.sf.ehcache.util.ManagementAttribute;
import net.sf.ehcache.util.counter.sampled.SampledCounter;
import net.sf.ehcache.util.counter.sampled.SampledRateCounter;

public interface CacheSampler
extends LegacyCacheStatistics {
    @ManagementAttribute
    public boolean isEnabled();

    public void setNodeBulkLoadEnabled(boolean var1);

    public boolean isClusterBulkLoadEnabled();

    @ManagementAttribute
    public boolean isNodeBulkLoadEnabled();

    public void setEnabled(boolean var1);

    public void removeAll();

    public void flush();

    @ManagementAttribute
    public String getStatus();

    @ManagementAttribute
    public boolean isTerracottaClustered();

    @ManagementAttribute
    public String getTerracottaConsistency();

    @ManagementAttribute
    public long getMaxEntriesLocalHeap();

    public void setMaxEntriesLocalHeap(long var1);

    @ManagementAttribute
    public long getMaxBytesLocalHeap();

    public void setMaxBytesLocalHeap(long var1);

    public void setMaxBytesLocalHeapAsString(String var1);

    @ManagementAttribute
    public String getMaxBytesLocalHeapAsString();

    @ManagementAttribute
    public long getMaxBytesLocalOffHeap();

    @ManagementAttribute
    public String getMaxBytesLocalOffHeapAsString();

    @ManagementAttribute
    public long getMaxEntriesLocalDisk();

    public void setMaxEntriesLocalDisk(long var1);

    @ManagementAttribute
    public int getMaxElementsOnDisk();

    @ManagementAttribute
    public long getMaxEntriesInCache();

    public void setMaxElementsOnDisk(int var1);

    public void setMaxEntriesInCache(long var1);

    @ManagementAttribute
    public long getMaxBytesLocalDisk();

    public void setMaxBytesLocalDisk(long var1);

    public void setMaxBytesLocalDiskAsString(String var1);

    @ManagementAttribute
    public String getMaxBytesLocalDiskAsString();

    @ManagementAttribute
    public String getMemoryStoreEvictionPolicy();

    public void setMemoryStoreEvictionPolicy(String var1);

    @ManagementAttribute
    public boolean isEternal();

    public void setEternal(boolean var1);

    @ManagementAttribute
    public long getTimeToIdleSeconds();

    public void setTimeToIdleSeconds(long var1);

    @ManagementAttribute
    public long getTimeToLiveSeconds();

    public void setTimeToLiveSeconds(long var1);

    @ManagementAttribute
    public boolean isOverflowToDisk();

    public void setOverflowToDisk(boolean var1);

    @ManagementAttribute
    public boolean isDiskPersistent();

    public void setDiskPersistent(boolean var1);

    @ManagementAttribute
    public boolean isOverflowToOffHeap();

    @ManagementAttribute
    public String getPersistenceStrategy();

    @ManagementAttribute
    public long getDiskExpiryThreadIntervalSeconds();

    public void setDiskExpiryThreadIntervalSeconds(long var1);

    @ManagementAttribute
    public boolean isLoggingEnabled();

    public void setLoggingEnabled(boolean var1);

    @ManagementAttribute
    public boolean isPinned();

    @ManagementAttribute
    public String getPinnedToStore();

    @ManagementAttribute
    public boolean getHasWriteBehindWriter();

    @Override
    public long getWriterQueueLength();

    public long getMostRecentRejoinTimeStampMillis();

    @ManagementAttribute
    public int getWriterMaxQueueSize();

    @ManagementAttribute
    public int getWriterConcurrency();

    @ManagementAttribute
    public boolean getTransactional();

    public long getTransactionCommitRate();

    public long getTransactionRollbackRate();

    @ManagementAttribute
    public boolean getSearchable();

    @ManagementAttribute
    public Map<String, String> getSearchAttributes();

    public long getCacheSearchRate();

    public long getAverageSearchTime();

    public long getCacheHitRate();

    public long getCacheInMemoryHitRate();

    public long getCacheOffHeapHitRate();

    public long getCacheOnDiskHitRate();

    public long getCacheMissRate();

    public long getCacheInMemoryMissRate();

    public long getCacheOffHeapMissRate();

    public long getCacheOnDiskMissRate();

    public long getCachePutRate();

    public long getCacheUpdateRate();

    public long getCacheRemoveRate();

    public long getCacheEvictionRate();

    public long getCacheExpirationRate();

    public long getAverageGetTime();

    public SampledCounter getCacheHitSample();

    public SampledCounter getCacheHitRatioSample();

    public SampledCounter getCacheHitInMemorySample();

    public SampledCounter getCacheHitOffHeapSample();

    public SampledCounter getCacheHitOnDiskSample();

    public SampledCounter getCacheMissSample();

    public SampledCounter getCacheMissInMemorySample();

    public SampledCounter getCacheMissOffHeapSample();

    public SampledCounter getCacheMissOnDiskSample();

    public SampledCounter getCacheMissExpiredSample();

    public SampledCounter getCacheMissNotFoundSample();

    public SampledCounter getCacheElementEvictedSample();

    public SampledCounter getCacheElementRemovedSample();

    public SampledCounter getCacheElementExpiredSample();

    public SampledCounter getCacheElementPutSample();

    public SampledCounter getCacheElementUpdatedSample();

    public SampledRateCounter getAverageGetTimeSample();

    public SampledRateCounter getAverageSearchTimeSample();

    public SampledCounter getSearchesPerSecondSample();

    public SampledCounter getCacheXaCommitsSample();

    public SampledCounter getCacheXaRollbacksSample();

    public SampledCounter getSizeSample();

    public SampledCounter getLocalHeapSizeSample();

    public SampledCounter getLocalHeapSizeInBytesSample();

    public SampledCounter getLocalOffHeapSizeSample();

    public SampledCounter getLocalOffHeapSizeInBytesSample();

    public SampledCounter getLocalDiskSizeSample();

    public SampledCounter getLocalDiskSizeInBytesSample();

    public SampledCounter getRemoteSizeSample();

    public SampledCounter getWriterQueueLengthSample();

    public SampledCounter getMostRecentRejoinTimestampMillisSample();

    public SampledCounter getCacheClusterOfflineSample();

    public SampledCounter getCacheClusterOnlineSample();

    public SampledCounter getCacheClusterRejoinSample();

    public SampledCounter getNonStopSuccessSample();

    public SampledCounter getNonStopFailureSample();

    public SampledCounter getNonStopRejoinTimeoutSample();

    public SampledCounter getNonStopTimeoutSample();

    public long getNonStopSuccessRate();

    public long getNonStopFailureRate();

    public long getNonStopRejoinTimeoutRate();

    public long getNonStopTimeoutRate();

    public SampledCounter getReplaceOneArgSuccessSample();

    public SampledCounter getReplaceOneArgMissSample();

    public SampledCounter getReplaceTwoArgSuccessSample();

    public SampledCounter getReplaceTwoArgMissSample();

    public SampledCounter getPutIfAbsentSuccessSample();

    public SampledCounter getPutIfAbsentMissSample();

    public SampledCounter getRemoveElementSuccessSample();

    public SampledCounter getRemoveElementMissSample();
}

