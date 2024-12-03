/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics;

import java.util.Arrays;
import java.util.EnumSet;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.statistics.CoreStatistics;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;

public class CoreStatisticsImpl
implements CoreStatistics {
    private final ExtendedStatistics extended;
    private final CoreStatistics.CountOperation cacheGet;
    private final CoreStatistics.CountOperation cachePut;
    private final CoreStatistics.CountOperation cacheRemove;
    private final CoreStatistics.CountOperation cacheReplace1;
    private final CoreStatistics.CountOperation cacheReplace2;
    private final CoreStatistics.CountOperation cachePutIfAbsent;
    private final CoreStatistics.CountOperation cacheRemoveElement;
    private final CoreStatistics.CountOperation localHeapGet;
    private final CoreStatistics.CountOperation localHeapPut;
    private final CoreStatistics.CountOperation localHeapRemove;
    private final CoreStatistics.CountOperation localOffHeapGet;
    private final CoreStatistics.CountOperation localOffHeapPut;
    private final CoreStatistics.CountOperation localOffHeapRemove;
    private final CoreStatistics.CountOperation localDiskGet;
    private final CoreStatistics.CountOperation localDiskPut;
    private final CoreStatistics.CountOperation localDiskRemove;
    private final CoreStatistics.CountOperation xaCommit;
    private final CoreStatistics.CountOperation xaRecovery;
    private final CoreStatistics.CountOperation xaRollback;
    private final CoreStatistics.CountOperation evicted;
    private final CoreStatistics.CountOperation expired;
    private final CoreStatistics.CountOperation cacheClusterEvent;

    public CoreStatisticsImpl(ExtendedStatistics extended) {
        this.extended = extended;
        this.cacheGet = CoreStatisticsImpl.asCountOperation(extended.get());
        this.cachePut = CoreStatisticsImpl.asCountOperation(extended.put());
        this.cacheRemove = CoreStatisticsImpl.asCountOperation(extended.remove());
        this.cacheReplace1 = CoreStatisticsImpl.asCountOperation(extended.replaceOneArg());
        this.cacheReplace2 = CoreStatisticsImpl.asCountOperation(extended.replaceOneArg());
        this.cachePutIfAbsent = CoreStatisticsImpl.asCountOperation(extended.putIfAbsent());
        this.cacheRemoveElement = CoreStatisticsImpl.asCountOperation(extended.removeElement());
        this.localHeapGet = CoreStatisticsImpl.asCountOperation(extended.heapGet());
        this.localHeapPut = CoreStatisticsImpl.asCountOperation(extended.heapPut());
        this.localHeapRemove = CoreStatisticsImpl.asCountOperation(extended.heapRemove());
        this.localOffHeapGet = CoreStatisticsImpl.asCountOperation(extended.offheapGet());
        this.localOffHeapPut = CoreStatisticsImpl.asCountOperation(extended.offheapPut());
        this.localOffHeapRemove = CoreStatisticsImpl.asCountOperation(extended.offheapRemove());
        this.localDiskGet = CoreStatisticsImpl.asCountOperation(extended.diskGet());
        this.localDiskPut = CoreStatisticsImpl.asCountOperation(extended.diskPut());
        this.localDiskRemove = CoreStatisticsImpl.asCountOperation(extended.diskRemove());
        this.xaCommit = CoreStatisticsImpl.asCountOperation(extended.xaCommit());
        this.xaRecovery = CoreStatisticsImpl.asCountOperation(extended.xaRecovery());
        this.xaRollback = CoreStatisticsImpl.asCountOperation(extended.xaRollback());
        this.evicted = CoreStatisticsImpl.asCountOperation(extended.eviction());
        this.expired = CoreStatisticsImpl.asCountOperation(extended.expiry());
        this.cacheClusterEvent = CoreStatisticsImpl.asCountOperation(extended.clusterEvent());
    }

    private static <T extends Enum<T>> CoreStatistics.CountOperation asCountOperation(final ExtendedStatistics.Operation<T> compoundOp) {
        return new CoreStatistics.CountOperation<T>(){

            @Override
            public long value(T result) {
                return compoundOp.component(result).count().value();
            }

            @Override
            public long value(T ... results) {
                return compoundOp.compound(EnumSet.copyOf(Arrays.asList(results))).count().value();
            }
        };
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.GetOutcome> get() {
        return this.cacheGet;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.PutOutcome> put() {
        return this.cachePut;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.RemoveOutcome> remove() {
        return this.cacheRemove;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.ReplaceOneArgOutcome> replaceOneArg() {
        return this.cacheReplace1;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.ReplaceTwoArgOutcome> replaceTwoArg() {
        return this.cacheReplace2;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.PutIfAbsentOutcome> putIfAbsent() {
        return this.cachePutIfAbsent;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.RemoveElementOutcome> removeElement() {
        return this.cacheRemoveElement;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.GetOutcome> localHeapGet() {
        return this.localHeapGet;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.PutOutcome> localHeapPut() {
        return this.localHeapPut;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.RemoveOutcome> localHeapRemove() {
        return this.localHeapRemove;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.GetOutcome> localOffHeapGet() {
        return this.localOffHeapGet;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.PutOutcome> localOffHeapPut() {
        return this.localOffHeapPut;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.RemoveOutcome> localOffHeapRemove() {
        return this.localOffHeapRemove;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.GetOutcome> localDiskGet() {
        return this.localDiskGet;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.PutOutcome> localDiskPut() {
        return this.localDiskPut;
    }

    @Override
    public CoreStatistics.CountOperation<StoreOperationOutcomes.RemoveOutcome> localDiskRemove() {
        return this.localDiskRemove;
    }

    @Override
    public CoreStatistics.CountOperation<XaCommitOutcome> xaCommit() {
        return this.xaCommit;
    }

    @Override
    public CoreStatistics.CountOperation<XaRecoveryOutcome> xaRecovery() {
        return this.xaRecovery;
    }

    @Override
    public CoreStatistics.CountOperation<XaRollbackOutcome> xaRollback() {
        return this.xaRollback;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.EvictionOutcome> cacheEviction() {
        return this.evicted;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.ExpiredOutcome> cacheExpiration() {
        return this.expired;
    }

    @Override
    public CoreStatistics.CountOperation<CacheOperationOutcomes.ClusterEventOutcomes> cacheClusterEvent() {
        return this.cacheClusterEvent;
    }
}

