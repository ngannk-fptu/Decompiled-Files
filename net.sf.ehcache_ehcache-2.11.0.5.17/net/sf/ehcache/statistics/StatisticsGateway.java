/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.statistics.CoreStatistics;
import net.sf.ehcache.statistics.CoreStatisticsImpl;
import net.sf.ehcache.statistics.FlatStatistics;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.ExtendedStatisticsImpl;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;
import org.terracotta.statistics.StatisticsManager;

public class StatisticsGateway
implements FlatStatistics {
    public static final int DEFAULT_HISTORY_SIZE = 30;
    public static final int DEFAULT_INTERVAL_SECS = 1;
    public static final int DEFAULT_SEARCH_INTERVAL_SECS = 10;
    public static final long DEFAULT_WINDOW_SIZE_SECS = 1L;
    private static final int DEFAULT_TIME_TO_DISABLE_MINS = 5;
    private final CoreStatistics core;
    private final ExtendedStatisticsImpl extended;
    private final String assocCacheName;

    public StatisticsGateway(Ehcache ehcache, ScheduledExecutorService executor) {
        StatisticsManager statsManager = new StatisticsManager();
        statsManager.root(ehcache);
        this.assocCacheName = ehcache.getName();
        ManagementRESTServiceConfiguration mRest = null;
        if (ehcache != null && ehcache.getCacheManager() != null && ehcache.getCacheManager().getConfiguration() != null) {
            mRest = ehcache.getCacheManager().getConfiguration().getManagementRESTService();
        }
        this.extended = new ExtendedStatisticsImpl(statsManager, executor, 5L, TimeUnit.MINUTES, this.getProperSampleHistorySize(mRest), this.getProperSampleIntervalSeconds(mRest), this.getProperSampleSearchIntervalSeconds(mRest));
        this.core = new CoreStatisticsImpl(this.extended);
    }

    private int getProperSampleSearchIntervalSeconds(ManagementRESTServiceConfiguration mRest) {
        return mRest == null ? 10 : mRest.getSampleSearchIntervalSeconds();
    }

    private int getProperSampleIntervalSeconds(ManagementRESTServiceConfiguration mRest) {
        return mRest == null ? 1 : mRest.getSampleIntervalSeconds();
    }

    private int getProperSampleHistorySize(ManagementRESTServiceConfiguration mRest) {
        return mRest == null ? 30 : mRest.getSampleHistorySize();
    }

    public CoreStatistics getCore() {
        return this.core;
    }

    public ExtendedStatistics getExtended() {
        return this.extended;
    }

    public void dispose() {
        this.extended.dispose();
    }

    public String getAssociatedCacheName() {
        return this.assocCacheName;
    }

    @Override
    public void setStatisticsTimeToDisable(long time, TimeUnit unit) {
        this.extended.setTimeToDisable(time, unit);
    }

    @Override
    public ExtendedStatistics.Result cacheGetOperation() {
        return this.extended.allGet();
    }

    @Override
    public ExtendedStatistics.Result cacheHitOperation() {
        return this.extended.get().component(CacheOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public ExtendedStatistics.Result cacheMissExpiredOperation() {
        return this.extended.get().component(CacheOperationOutcomes.GetOutcome.MISS_EXPIRED);
    }

    @Override
    public ExtendedStatistics.Result cacheMissNotFoundOperation() {
        return this.extended.get().component(CacheOperationOutcomes.GetOutcome.MISS_NOT_FOUND);
    }

    @Override
    public ExtendedStatistics.Result cacheMissOperation() {
        return this.extended.allMiss();
    }

    @Override
    public ExtendedStatistics.Result cachePutAddedOperation() {
        return this.extended.put().component(CacheOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public ExtendedStatistics.Result cachePutReplacedOperation() {
        return this.extended.put().component(CacheOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public ExtendedStatistics.Result cachePutOperation() {
        return this.extended.allPut();
    }

    @Override
    public ExtendedStatistics.Result cacheRemoveOperation() {
        return this.extended.remove().component(CacheOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public ExtendedStatistics.Result localHeapHitOperation() {
        return this.extended.heapGet().component(StoreOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public ExtendedStatistics.Result localHeapMissOperation() {
        return this.extended.heapGet().component(StoreOperationOutcomes.GetOutcome.MISS);
    }

    @Override
    public ExtendedStatistics.Result localHeapPutAddedOperation() {
        return this.extended.heapPut().component(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public ExtendedStatistics.Result localHeapPutReplacedOperation() {
        return this.extended.heapPut().component(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public ExtendedStatistics.Result localHeapPutOperation() {
        return this.extended.heapAllPut();
    }

    @Override
    public ExtendedStatistics.Result localHeapRemoveOperation() {
        return this.extended.heapRemove().component(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public ExtendedStatistics.Result localOffHeapHitOperation() {
        return this.extended.offheapGet().component(StoreOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public ExtendedStatistics.Result localOffHeapMissOperation() {
        return this.extended.offheapGet().component(StoreOperationOutcomes.GetOutcome.MISS);
    }

    @Override
    public ExtendedStatistics.Result localOffHeapPutAddedOperation() {
        return this.extended.offheapPut().component(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public ExtendedStatistics.Result localOffHeapPutReplacedOperation() {
        return this.extended.offheapPut().component(StoreOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public ExtendedStatistics.Result localOffHeapPutOperation() {
        return this.extended.offHeapAllPut();
    }

    @Override
    public ExtendedStatistics.Result localOffHeapRemoveOperation() {
        return this.extended.offheapRemove().component(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public ExtendedStatistics.Result localDiskHitOperation() {
        return this.extended.diskGet().component(StoreOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public ExtendedStatistics.Result localDiskMissOperation() {
        return this.extended.diskGet().component(StoreOperationOutcomes.GetOutcome.MISS);
    }

    @Override
    public ExtendedStatistics.Result localDiskPutAddedOperation() {
        return this.extended.diskPut().component(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public ExtendedStatistics.Result localDiskPutReplacedOperation() {
        return this.extended.diskPut().component(StoreOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public ExtendedStatistics.Result localDiskPutOperation() {
        return this.extended.diskAllPut();
    }

    @Override
    public ExtendedStatistics.Result localDiskRemoveOperation() {
        return this.extended.diskRemove().component(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public ExtendedStatistics.Result cacheSearchOperation() {
        return this.extended.search().component(CacheOperationOutcomes.SearchOutcome.SUCCESS);
    }

    @Override
    public ExtendedStatistics.Result xaCommitSuccessOperation() {
        return this.extended.xaCommit().component(XaCommitOutcome.COMMITTED);
    }

    @Override
    public ExtendedStatistics.Result xaCommitExceptionOperation() {
        return this.extended.xaCommit().component(XaCommitOutcome.EXCEPTION);
    }

    @Override
    public ExtendedStatistics.Result xaCommitReadOnlyOperation() {
        return this.extended.xaCommit().component(XaCommitOutcome.READ_ONLY);
    }

    @Override
    public ExtendedStatistics.Result xaRollbackOperation() {
        return this.extended.xaRollback().component(XaRollbackOutcome.ROLLEDBACK);
    }

    @Override
    public ExtendedStatistics.Result xaRollbackExceptionOperation() {
        return this.extended.xaRollback().component(XaRollbackOutcome.EXCEPTION);
    }

    @Override
    public ExtendedStatistics.Result xaRecoveryOperation() {
        return this.extended.xaRecovery().component(XaRecoveryOutcome.RECOVERED);
    }

    @Override
    public ExtendedStatistics.Result cacheEvictionOperation() {
        return this.extended.eviction().component(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
    }

    @Override
    public ExtendedStatistics.Result cacheExpiredOperation() {
        return this.extended.expiry().component(CacheOperationOutcomes.ExpiredOutcome.SUCCESS);
    }

    @Override
    public long getLocalHeapSizeInBytes() {
        return this.extended.localHeapSizeInBytes().value().longValue();
    }

    @Override
    public long getLocalHeapSize() {
        return this.extended.localHeapSize().value().longValue();
    }

    @Override
    public long getWriterQueueLength() {
        return this.extended.writerQueueLength().value().longValue();
    }

    @Override
    public long getLocalDiskSize() {
        return this.extended.localDiskSize().value().longValue();
    }

    @Override
    public long getLocalOffHeapSize() {
        return this.extended.localOffHeapSize().value().longValue();
    }

    @Override
    public long getLocalDiskSizeInBytes() {
        return this.extended.localDiskSizeInBytes().value().longValue();
    }

    @Override
    public long getLocalOffHeapSizeInBytes() {
        return this.extended.localOffHeapSizeInBytes().value().longValue();
    }

    @Override
    public long getRemoteSize() {
        return this.extended.remoteSize().value().longValue();
    }

    @Override
    public long getSize() {
        return this.extended.size().value().longValue();
    }

    @Override
    public long cacheHitCount() {
        return this.core.get().value(CacheOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public long cacheMissExpiredCount() {
        return this.core.get().value(CacheOperationOutcomes.GetOutcome.MISS_EXPIRED);
    }

    @Override
    public long cacheMissNotFoundCount() {
        return this.core.get().value(CacheOperationOutcomes.GetOutcome.MISS_NOT_FOUND);
    }

    @Override
    public long cacheMissCount() {
        return this.cacheMissExpiredCount() + this.cacheMissNotFoundCount();
    }

    @Override
    public long cachePutAddedCount() {
        return this.core.put().value(CacheOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public long cachePutUpdatedCount() {
        return this.core.put().value(CacheOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public long cachePutCount() {
        return this.cachePutAddedCount() + this.cachePutUpdatedCount();
    }

    @Override
    public long cacheRemoveCount() {
        return this.core.remove().value(CacheOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public long localHeapHitCount() {
        return this.core.localHeapGet().value(StoreOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public long localHeapMissCount() {
        return this.core.localHeapGet().value(StoreOperationOutcomes.GetOutcome.MISS);
    }

    @Override
    public long localHeapPutAddedCount() {
        return this.core.localHeapPut().value(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public long localHeapPutUpdatedCount() {
        return this.core.localHeapPut().value(StoreOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public long localHeapPutCount() {
        return this.localHeapPutAddedCount() + this.localHeapPutUpdatedCount();
    }

    @Override
    public long localHeapRemoveCount() {
        return this.core.localHeapRemove().value(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public long localOffHeapHitCount() {
        return this.core.localOffHeapGet().value(StoreOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public long localOffHeapMissCount() {
        return this.core.localOffHeapGet().value(StoreOperationOutcomes.GetOutcome.MISS);
    }

    @Override
    public long localOffHeapPutAddedCount() {
        return this.core.localOffHeapPut().value(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public long localOffHeapPutUpdatedCount() {
        return this.core.localOffHeapPut().value(StoreOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public long localOffHeapPutCount() {
        return this.localOffHeapPutAddedCount() + this.localOffHeapPutUpdatedCount();
    }

    @Override
    public long localOffHeapRemoveCount() {
        return this.core.localOffHeapRemove().value(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public long localDiskHitCount() {
        return this.core.localDiskGet().value(StoreOperationOutcomes.GetOutcome.HIT);
    }

    @Override
    public long localDiskMissCount() {
        return this.core.localDiskGet().value(StoreOperationOutcomes.GetOutcome.MISS);
    }

    @Override
    public long localDiskPutAddedCount() {
        return this.core.localDiskPut().value(StoreOperationOutcomes.PutOutcome.ADDED);
    }

    @Override
    public long localDiskPutUpdatedCount() {
        return this.core.localDiskPut().value(StoreOperationOutcomes.PutOutcome.UPDATED);
    }

    @Override
    public long localDiskPutCount() {
        return this.localDiskPutAddedCount() + this.localDiskPutUpdatedCount();
    }

    @Override
    public long localDiskRemoveCount() {
        return this.core.localDiskRemove().value(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
    }

    @Override
    public long xaCommitReadOnlyCount() {
        return this.core.xaCommit().value(XaCommitOutcome.READ_ONLY);
    }

    @Override
    public long xaCommitExceptionCount() {
        return this.core.xaCommit().value(XaCommitOutcome.EXCEPTION);
    }

    @Override
    public long xaCommitCommittedCount() {
        return this.core.xaCommit().value(XaCommitOutcome.COMMITTED);
    }

    @Override
    public long xaCommitCount() {
        return this.xaCommitCommittedCount() + this.xaCommitExceptionCount() + this.xaCommitReadOnlyCount();
    }

    @Override
    public long xaRecoveryNothingCount() {
        return this.core.xaRecovery().value(XaRecoveryOutcome.NOTHING);
    }

    @Override
    public long xaRecoveryRecoveredCount() {
        return this.core.xaRecovery().value(XaRecoveryOutcome.RECOVERED);
    }

    @Override
    public long xaRecoveryCount() {
        return this.xaRecoveryNothingCount() + this.xaRecoveryRecoveredCount();
    }

    @Override
    public long xaRollbackExceptionCount() {
        return this.core.xaRollback().value(XaRollbackOutcome.EXCEPTION);
    }

    @Override
    public long xaRollbackSuccessCount() {
        return this.core.xaRollback().value(XaRollbackOutcome.ROLLEDBACK);
    }

    @Override
    public long xaRollbackCount() {
        return this.xaRollbackExceptionCount() + this.xaRollbackSuccessCount();
    }

    @Override
    public long cacheExpiredCount() {
        return this.core.cacheExpiration().value(CacheOperationOutcomes.ExpiredOutcome.SUCCESS);
    }

    @Override
    public long cacheEvictedCount() {
        return this.core.cacheEviction().value(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
    }

    @Override
    public double cacheHitRatio() {
        return this.extended.cacheHitRatio().value();
    }
}

