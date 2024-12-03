/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics;

import java.util.concurrent.TimeUnit;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;

public interface FlatStatistics {
    public double cacheHitRatio();

    public long cacheHitCount();

    public long cacheMissExpiredCount();

    public long cacheMissNotFoundCount();

    public long cacheMissCount();

    public long cachePutAddedCount();

    public long cachePutUpdatedCount();

    public long cachePutCount();

    public long cacheRemoveCount();

    public long localHeapHitCount();

    public long localHeapMissCount();

    public long localHeapPutAddedCount();

    public long localHeapPutUpdatedCount();

    public long localHeapPutCount();

    public long localHeapRemoveCount();

    public long localOffHeapHitCount();

    public long localOffHeapMissCount();

    public long localOffHeapPutAddedCount();

    public long localOffHeapPutUpdatedCount();

    public long localOffHeapPutCount();

    public long localOffHeapRemoveCount();

    public long localDiskHitCount();

    public long localDiskMissCount();

    public long localDiskPutAddedCount();

    public long localDiskPutUpdatedCount();

    public long localDiskPutCount();

    public long localDiskRemoveCount();

    public long xaCommitReadOnlyCount();

    public long xaCommitExceptionCount();

    public long xaCommitCommittedCount();

    public long xaCommitCount();

    public long xaRecoveryNothingCount();

    public long xaRecoveryRecoveredCount();

    public long xaRecoveryCount();

    public long xaRollbackExceptionCount();

    public long xaRollbackSuccessCount();

    public long xaRollbackCount();

    public long cacheExpiredCount();

    public long cacheEvictedCount();

    public void setStatisticsTimeToDisable(long var1, TimeUnit var3);

    public ExtendedStatistics.Result cacheGetOperation();

    public ExtendedStatistics.Result cacheHitOperation();

    public ExtendedStatistics.Result cacheMissExpiredOperation();

    public ExtendedStatistics.Result cacheMissNotFoundOperation();

    public ExtendedStatistics.Result cacheMissOperation();

    public ExtendedStatistics.Result cachePutAddedOperation();

    public ExtendedStatistics.Result cachePutReplacedOperation();

    public ExtendedStatistics.Result cachePutOperation();

    public ExtendedStatistics.Result cacheRemoveOperation();

    public ExtendedStatistics.Result localHeapHitOperation();

    public ExtendedStatistics.Result localHeapMissOperation();

    public ExtendedStatistics.Result localHeapPutAddedOperation();

    public ExtendedStatistics.Result localHeapPutReplacedOperation();

    public ExtendedStatistics.Result localHeapPutOperation();

    public ExtendedStatistics.Result localHeapRemoveOperation();

    public ExtendedStatistics.Result localOffHeapHitOperation();

    public ExtendedStatistics.Result localOffHeapMissOperation();

    public ExtendedStatistics.Result localOffHeapPutAddedOperation();

    public ExtendedStatistics.Result localOffHeapPutReplacedOperation();

    public ExtendedStatistics.Result localOffHeapPutOperation();

    public ExtendedStatistics.Result localOffHeapRemoveOperation();

    public ExtendedStatistics.Result localDiskHitOperation();

    public ExtendedStatistics.Result localDiskMissOperation();

    public ExtendedStatistics.Result localDiskPutAddedOperation();

    public ExtendedStatistics.Result localDiskPutReplacedOperation();

    public ExtendedStatistics.Result localDiskPutOperation();

    public ExtendedStatistics.Result localDiskRemoveOperation();

    public ExtendedStatistics.Result cacheSearchOperation();

    public ExtendedStatistics.Result xaCommitSuccessOperation();

    public ExtendedStatistics.Result xaCommitExceptionOperation();

    public ExtendedStatistics.Result xaCommitReadOnlyOperation();

    public ExtendedStatistics.Result xaRollbackOperation();

    public ExtendedStatistics.Result xaRollbackExceptionOperation();

    public ExtendedStatistics.Result xaRecoveryOperation();

    public ExtendedStatistics.Result cacheEvictionOperation();

    public ExtendedStatistics.Result cacheExpiredOperation();

    public long getSize();

    public long getLocalHeapSize();

    public long getLocalHeapSizeInBytes();

    public long getLocalOffHeapSize();

    public long getLocalOffHeapSizeInBytes();

    public long getLocalDiskSize();

    public long getLocalDiskSizeInBytes();

    public long getRemoteSize();

    public long getWriterQueueLength();
}

