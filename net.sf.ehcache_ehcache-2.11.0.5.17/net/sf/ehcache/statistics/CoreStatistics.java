/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics;

import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;

public interface CoreStatistics {
    public CountOperation<CacheOperationOutcomes.GetOutcome> get();

    public CountOperation<CacheOperationOutcomes.PutOutcome> put();

    public CountOperation<CacheOperationOutcomes.RemoveOutcome> remove();

    public CountOperation<CacheOperationOutcomes.ReplaceOneArgOutcome> replaceOneArg();

    public CountOperation<CacheOperationOutcomes.ReplaceTwoArgOutcome> replaceTwoArg();

    public CountOperation<CacheOperationOutcomes.PutIfAbsentOutcome> putIfAbsent();

    public CountOperation<CacheOperationOutcomes.RemoveElementOutcome> removeElement();

    public CountOperation<StoreOperationOutcomes.GetOutcome> localHeapGet();

    public CountOperation<StoreOperationOutcomes.PutOutcome> localHeapPut();

    public CountOperation<StoreOperationOutcomes.RemoveOutcome> localHeapRemove();

    public CountOperation<StoreOperationOutcomes.GetOutcome> localOffHeapGet();

    public CountOperation<StoreOperationOutcomes.PutOutcome> localOffHeapPut();

    public CountOperation<StoreOperationOutcomes.RemoveOutcome> localOffHeapRemove();

    public CountOperation<StoreOperationOutcomes.GetOutcome> localDiskGet();

    public CountOperation<StoreOperationOutcomes.PutOutcome> localDiskPut();

    public CountOperation<StoreOperationOutcomes.RemoveOutcome> localDiskRemove();

    public CountOperation<XaCommitOutcome> xaCommit();

    public CountOperation<XaRecoveryOutcome> xaRecovery();

    public CountOperation<XaRollbackOutcome> xaRollback();

    public CountOperation<CacheOperationOutcomes.EvictionOutcome> cacheEviction();

    public CountOperation<CacheOperationOutcomes.ExpiredOutcome> cacheExpiration();

    public CountOperation<CacheOperationOutcomes.ClusterEventOutcomes> cacheClusterEvent();

    public static interface CountOperation<T> {
        public long value(T var1);

        public long value(T ... var1);
    }
}

