/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;
import org.terracotta.statistics.archive.Timestamped;

public interface ExtendedStatistics {
    public static final Set<CacheOperationOutcomes.PutOutcome> ALL_CACHE_PUT_OUTCOMES = EnumSet.allOf(CacheOperationOutcomes.PutOutcome.class);
    public static final Set<CacheOperationOutcomes.GetOutcome> ALL_CACHE_GET_OUTCOMES = EnumSet.allOf(CacheOperationOutcomes.GetOutcome.class);
    public static final Set<CacheOperationOutcomes.GetOutcome> ALL_CACHE_MISS_OUTCOMES = EnumSet.of(CacheOperationOutcomes.GetOutcome.MISS_EXPIRED, CacheOperationOutcomes.GetOutcome.MISS_NOT_FOUND);
    public static final Set<StoreOperationOutcomes.PutOutcome> ALL_STORE_PUT_OUTCOMES = EnumSet.allOf(StoreOperationOutcomes.PutOutcome.class);

    public void setTimeToDisable(long var1, TimeUnit var3);

    public void setAlwaysOn(boolean var1);

    public Operation<CacheOperationOutcomes.GetOutcome> get();

    public Operation<CacheOperationOutcomes.PutOutcome> put();

    public Operation<CacheOperationOutcomes.RemoveOutcome> remove();

    public Operation<CacheOperationOutcomes.ReplaceOneArgOutcome> replaceOneArg();

    public Operation<CacheOperationOutcomes.ReplaceTwoArgOutcome> replaceTwoArg();

    public Operation<CacheOperationOutcomes.PutIfAbsentOutcome> putIfAbsent();

    public Operation<CacheOperationOutcomes.RemoveElementOutcome> removeElement();

    public Operation<StoreOperationOutcomes.GetOutcome> heapGet();

    public Operation<StoreOperationOutcomes.GetOutcome> offheapGet();

    public Operation<StoreOperationOutcomes.GetOutcome> diskGet();

    public Operation<StoreOperationOutcomes.PutOutcome> heapPut();

    public Operation<StoreOperationOutcomes.PutOutcome> offheapPut();

    public Operation<StoreOperationOutcomes.PutOutcome> diskPut();

    public Operation<StoreOperationOutcomes.RemoveOutcome> heapRemove();

    public Operation<StoreOperationOutcomes.RemoveOutcome> offheapRemove();

    public Operation<StoreOperationOutcomes.RemoveOutcome> diskRemove();

    public Operation<CacheOperationOutcomes.SearchOutcome> search();

    public Operation<XaCommitOutcome> xaCommit();

    public Operation<XaRollbackOutcome> xaRollback();

    public Operation<XaRecoveryOutcome> xaRecovery();

    public Operation<CacheOperationOutcomes.EvictionOutcome> eviction();

    public Operation<CacheOperationOutcomes.ExpiredOutcome> expiry();

    public Operation<CacheOperationOutcomes.ClusterEventOutcomes> clusterEvent();

    public Operation<CacheOperationOutcomes.NonStopOperationOutcomes> nonstop();

    public Result allGet();

    public Result allMiss();

    public Result allPut();

    public Result heapAllPut();

    public Result offHeapAllPut();

    public Result diskAllPut();

    public Statistic<Double> cacheHitRatio();

    public Statistic<Double> nonstopTimeoutRatio();

    public <T extends Enum<T>> Set<Operation<T>> operations(Class<T> var1, String var2, String ... var3);

    public Set<Statistic<Number>> passthru(String var1, Set<String> var2);

    public Statistic<Number> size();

    public Statistic<Number> localHeapSize();

    public Statistic<Number> localHeapSizeInBytes();

    public Statistic<Number> localOffHeapSize();

    public Statistic<Number> localOffHeapSizeInBytes();

    public Statistic<Number> localDiskSize();

    public Statistic<Number> localDiskSizeInBytes();

    public Statistic<Number> remoteSize();

    public Statistic<Number> writerQueueLength();

    public Statistic<Number> mostRecentRejoinTimeStampMillis();

    public static interface Statistic<T extends Number> {
        public boolean active();

        public T value();

        public List<Timestamped<T>> history();
    }

    public static interface Latency {
        public Statistic<Long> minimum();

        public Statistic<Long> maximum();

        public Statistic<Double> average();
    }

    public static interface Result {
        public Statistic<Long> count();

        public Statistic<Double> rate();

        public Latency latency();
    }

    public static interface Operation<T extends Enum<T>> {
        public Class<T> type();

        public Result component(T var1);

        public Result compound(Set<T> var1);

        public Statistic<Double> ratioOf(Set<T> var1, Set<T> var2);

        public void setAlwaysOn(boolean var1);

        public boolean isAlwaysOn();

        public void setWindow(long var1, TimeUnit var3);

        public void setHistory(int var1, long var2, TimeUnit var4);

        public long getWindowSize(TimeUnit var1);

        public int getHistorySampleSize();

        public long getHistorySampleTime(TimeUnit var1);
    }
}

