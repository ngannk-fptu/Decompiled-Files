/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.statistics.extended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.statistics.extended.CompoundOperationImpl;
import net.sf.ehcache.statistics.extended.EhcacheQueryBuilder;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.NullCompoundOperation;
import net.sf.ehcache.statistics.extended.NullStatistic;
import net.sf.ehcache.statistics.extended.SemiExpiringStatistic;
import net.sf.ehcache.statistics.extended.StandardOperationStatistic;
import net.sf.ehcache.statistics.extended.StandardPassThroughStatistic;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Matcher;
import org.terracotta.context.query.Matchers;
import org.terracotta.context.query.Query;
import org.terracotta.context.query.QueryBuilder;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.ValueStatistic;

public class ExtendedStatisticsImpl
implements ExtendedStatistics {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedStatisticsImpl.class);
    private final ConcurrentMap<StandardPassThroughStatistic, ExtendedStatistics.Statistic<Number>> standardPassThroughs = new ConcurrentHashMap<StandardPassThroughStatistic, ExtendedStatistics.Statistic<Number>>();
    private final ConcurrentMap<StandardOperationStatistic, ExtendedStatistics.Operation<?>> standardOperations = new ConcurrentHashMap();
    private final ConcurrentMap<OperationStatistic<?>, CompoundOperationImpl<?>> customOperations = new ConcurrentHashMap();
    private final ConcurrentHashMap<Collection<String>, Set<ExtendedStatistics.Statistic<Number>>> customPassthrus = new ConcurrentHashMap();
    private final StatisticsManager manager;
    private final ScheduledExecutorService executor;
    private final Runnable disableTask = new Runnable(){

        @Override
        public void run() {
            long expireThreshold = Time.absoluteTime() - ExtendedStatisticsImpl.this.timeToDisableUnit.toMillis(ExtendedStatisticsImpl.this.timeToDisable);
            for (ExtendedStatistics.Operation o : ExtendedStatisticsImpl.this.standardOperations.values()) {
                if (!(o instanceof CompoundOperationImpl)) continue;
                ((CompoundOperationImpl)o).expire(expireThreshold);
            }
            Iterator it = ExtendedStatisticsImpl.this.customOperations.values().iterator();
            while (it.hasNext()) {
                if (!((CompoundOperationImpl)it.next()).expire(expireThreshold)) continue;
                it.remove();
            }
        }
    };
    private long timeToDisable;
    private TimeUnit timeToDisableUnit;
    private ScheduledFuture disableStatus;
    private final ExtendedStatistics.Result allCacheGet;
    private final ExtendedStatistics.Result allCacheMiss;
    private final ExtendedStatistics.Result allCachePut;
    private final ExtendedStatistics.Result allHeapPut;
    private final ExtendedStatistics.Result allOffHeapPut;
    private final ExtendedStatistics.Result allDiskPut;
    private ExtendedStatistics.Statistic<Double> cacheHitRatio;
    private ExtendedStatistics.Statistic<Double> nonStopTimeoutRatio;
    private final int defaultHistorySize;
    private final long defaultIntervalSeconds;
    private final long defaultSearchIntervalSeconds;

    public ExtendedStatisticsImpl(StatisticsManager manager, ScheduledExecutorService executor, long timeToDisable, TimeUnit unit, int defaultHistorySize, long defaultIntervalSeconds, long defaultSearchIntervalSeconds) {
        this.manager = manager;
        this.executor = executor;
        this.timeToDisable = timeToDisable;
        this.timeToDisableUnit = unit;
        this.defaultHistorySize = defaultHistorySize;
        this.defaultIntervalSeconds = defaultIntervalSeconds;
        this.defaultSearchIntervalSeconds = defaultSearchIntervalSeconds;
        this.disableStatus = this.executor.scheduleAtFixedRate(this.disableTask, timeToDisable, timeToDisable, unit);
        this.findStandardPassThruStatistics();
        this.findStandardOperationStatistics();
        this.allCacheGet = this.get().compound(ALL_CACHE_GET_OUTCOMES);
        this.allCacheMiss = this.get().compound(ALL_CACHE_MISS_OUTCOMES);
        this.allCachePut = this.put().compound(ALL_CACHE_PUT_OUTCOMES);
        this.allHeapPut = this.heapPut().compound(ALL_STORE_PUT_OUTCOMES);
        this.allOffHeapPut = this.offheapPut().compound(ALL_STORE_PUT_OUTCOMES);
        this.allDiskPut = this.diskPut().compound(ALL_STORE_PUT_OUTCOMES);
        this.cacheHitRatio = this.get().ratioOf(EnumSet.of(CacheOperationOutcomes.GetOutcome.HIT), EnumSet.allOf(CacheOperationOutcomes.GetOutcome.class));
        this.nonStopTimeoutRatio = this.nonstop().ratioOf(EnumSet.of(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT, CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT), EnumSet.allOf(CacheOperationOutcomes.NonStopOperationOutcomes.class));
    }

    private void findStandardOperationStatistics() {
        for (StandardOperationStatistic t : StandardOperationStatistic.values()) {
            OperationStatistic statistic = ExtendedStatisticsImpl.findOperationStatistic(this.manager, t);
            if (statistic == null) {
                if (t.required()) {
                    throw new IllegalStateException("Required statistic " + t + " not found");
                }
                LOGGER.debug("Mocking Operation Statistic: {}", (Object)t);
                this.standardOperations.put(t, NullCompoundOperation.instance(t.type()));
                continue;
            }
            this.standardOperations.put(t, new CompoundOperationImpl<Enum>(statistic, t.type(), 1L, TimeUnit.SECONDS, this.executor, this.defaultHistorySize, t.isSearch() ? this.defaultSearchIntervalSeconds : this.defaultIntervalSeconds, TimeUnit.SECONDS));
        }
    }

    private void findStandardPassThruStatistics() {
        for (StandardPassThroughStatistic t : StandardPassThroughStatistic.values()) {
            ValueStatistic statistic = ExtendedStatisticsImpl.findPassThroughStatistic(this.manager, t);
            if (statistic == null) {
                LOGGER.debug("Mocking Pass-Through Statistic: {}", (Object)t);
                this.standardPassThroughs.put(t, NullStatistic.instance(t.absentValue()));
                continue;
            }
            this.standardPassThroughs.put(t, new SemiExpiringStatistic(statistic, this.executor, this.defaultHistorySize, TimeUnit.SECONDS.toNanos(this.defaultIntervalSeconds)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<ExtendedStatistics.Statistic<Number>> passthru(String name, Set<String> tags) {
        ArrayList<String> key = new ArrayList<String>(tags.size() + 1);
        key.addAll(tags);
        Collections.sort(key);
        key.add(name);
        if (this.customPassthrus.containsKey(key)) {
            return this.customPassthrus.get(key);
        }
        ConcurrentHashMap<Collection<String>, Set<ExtendedStatistics.Statistic<Number>>> concurrentHashMap = this.customPassthrus;
        synchronized (concurrentHashMap) {
            if (this.customPassthrus.containsKey(key)) {
                return this.customPassthrus.get(key);
            }
            Set<ValueStatistic<?>> interim = ExtendedStatisticsImpl.findPassThroughStatistic(this.manager, EhcacheQueryBuilder.cache().descendants(), name, tags);
            if (interim.isEmpty()) {
                return Collections.EMPTY_SET;
            }
            HashSet<ExtendedStatistics.Statistic<Number>> ret = new HashSet<ExtendedStatistics.Statistic<Number>>(interim.size());
            for (ValueStatistic<?> vs : interim) {
                SemiExpiringStatistic stat = new SemiExpiringStatistic(vs, this.executor, this.defaultHistorySize, TimeUnit.SECONDS.toNanos(this.defaultIntervalSeconds));
                ret.add(stat);
            }
            this.customPassthrus.put(key, ret);
            return ret;
        }
    }

    @Override
    public synchronized void setTimeToDisable(long time, TimeUnit unit) {
        this.timeToDisable = time;
        this.timeToDisableUnit = unit;
        if (this.disableStatus != null) {
            this.disableStatus.cancel(false);
            this.disableStatus = this.executor.scheduleAtFixedRate(this.disableTask, this.timeToDisable, this.timeToDisable, this.timeToDisableUnit);
        }
    }

    @Override
    public synchronized void setAlwaysOn(boolean enabled) {
        if (enabled) {
            if (this.disableStatus != null) {
                this.disableStatus.cancel(false);
                this.disableStatus = null;
            }
            for (ExtendedStatistics.Operation o : this.standardOperations.values()) {
                o.setAlwaysOn(true);
            }
        } else {
            if (this.disableStatus == null) {
                this.disableStatus = this.executor.scheduleAtFixedRate(this.disableTask, 0L, this.timeToDisable, this.timeToDisableUnit);
            }
            for (ExtendedStatistics.Operation o : this.standardOperations.values()) {
                o.setAlwaysOn(false);
            }
        }
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.GetOutcome> get() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_GET);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.PutOutcome> put() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_PUT);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.RemoveOutcome> remove() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_REMOVE);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.ReplaceOneArgOutcome> replaceOneArg() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_ONE_ARG_REPLACE);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.ReplaceTwoArgOutcome> replaceTwoArg() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_TWO_ARG_REPLACE);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.PutIfAbsentOutcome> putIfAbsent() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_PUT_IF_ABSENT);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.RemoveElementOutcome> removeElement() {
        return this.getStandardOperation(StandardOperationStatistic.CACHE_REMOVE_ELEMENT);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.SearchOutcome> search() {
        return this.getStandardOperation(StandardOperationStatistic.SEARCH);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.GetOutcome> heapGet() {
        return this.getStandardOperation(StandardOperationStatistic.HEAP_GET);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.PutOutcome> heapPut() {
        return this.getStandardOperation(StandardOperationStatistic.HEAP_PUT);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.RemoveOutcome> heapRemove() {
        return this.getStandardOperation(StandardOperationStatistic.HEAP_REMOVE);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.GetOutcome> offheapGet() {
        return this.getStandardOperation(StandardOperationStatistic.OFFHEAP_GET);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.PutOutcome> offheapPut() {
        return this.getStandardOperation(StandardOperationStatistic.OFFHEAP_PUT);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.RemoveOutcome> offheapRemove() {
        return this.getStandardOperation(StandardOperationStatistic.OFFHEAP_REMOVE);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.GetOutcome> diskGet() {
        return this.getStandardOperation(StandardOperationStatistic.DISK_GET);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.PutOutcome> diskPut() {
        return this.getStandardOperation(StandardOperationStatistic.DISK_PUT);
    }

    @Override
    public ExtendedStatistics.Operation<StoreOperationOutcomes.RemoveOutcome> diskRemove() {
        return this.getStandardOperation(StandardOperationStatistic.DISK_REMOVE);
    }

    @Override
    public ExtendedStatistics.Operation<XaCommitOutcome> xaCommit() {
        return this.getStandardOperation(StandardOperationStatistic.XA_COMMIT);
    }

    @Override
    public ExtendedStatistics.Operation<XaRollbackOutcome> xaRollback() {
        return this.getStandardOperation(StandardOperationStatistic.XA_ROLLBACK);
    }

    @Override
    public ExtendedStatistics.Operation<XaRecoveryOutcome> xaRecovery() {
        return this.getStandardOperation(StandardOperationStatistic.XA_RECOVERY);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.EvictionOutcome> eviction() {
        return this.getStandardOperation(StandardOperationStatistic.EVICTION);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.ExpiredOutcome> expiry() {
        return this.getStandardOperation(StandardOperationStatistic.EXPIRY);
    }

    @Override
    public ExtendedStatistics.Statistic<Double> cacheHitRatio() {
        return this.cacheHitRatio;
    }

    @Override
    public ExtendedStatistics.Result allGet() {
        return this.allCacheGet;
    }

    @Override
    public ExtendedStatistics.Result allMiss() {
        return this.allCacheMiss;
    }

    @Override
    public ExtendedStatistics.Result allPut() {
        return this.allCachePut;
    }

    @Override
    public ExtendedStatistics.Result heapAllPut() {
        return this.allHeapPut;
    }

    @Override
    public ExtendedStatistics.Result offHeapAllPut() {
        return this.allOffHeapPut;
    }

    @Override
    public ExtendedStatistics.Result diskAllPut() {
        return this.allDiskPut;
    }

    @Override
    public <T extends Enum<T>> Set<ExtendedStatistics.Operation<T>> operations(Class<T> outcome, String name, String ... tags) {
        Set<OperationStatistic<T>> sources = ExtendedStatisticsImpl.findOperationStatistic(this.manager, QueryBuilder.queryBuilder().descendants().build(), outcome, name, new HashSet<String>(Arrays.asList(tags)));
        if (sources.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet<ExtendedStatistics.Operation<T>> operations = new HashSet<ExtendedStatistics.Operation<T>>();
        for (OperationStatistic<T> source : sources) {
            CompoundOperationImpl<T> racer;
            CompoundOperationImpl<T> operation = (CompoundOperationImpl<T>)this.customOperations.get(source);
            if (operation == null && (racer = this.customOperations.putIfAbsent(source, operation = new CompoundOperationImpl<T>(source, source.type(), 1L, TimeUnit.SECONDS, this.executor, 0, 1L, TimeUnit.SECONDS))) != null) {
                operation = racer;
            }
            operations.add(operation);
        }
        return operations;
    }

    @Override
    public ExtendedStatistics.Statistic<Number> localHeapSize() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LOCAL_HEAP_SIZE);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> localHeapSizeInBytes() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LOCAL_HEAP_SIZE_BYTES);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> localOffHeapSize() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LOCAL_OFFHEAP_SIZE);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> localOffHeapSizeInBytes() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LOCAL_OFFHEAP_SIZE_BYTES);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> localDiskSize() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LOCAL_DISK_SIZE);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> localDiskSizeInBytes() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LOCAL_DISK_SIZE_BYTES);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> remoteSize() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.REMOTE_SIZE);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> size() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.CACHE_SIZE);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> writerQueueLength() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.WRITER_QUEUE_LENGTH);
    }

    private ExtendedStatistics.Operation<?> getStandardOperation(StandardOperationStatistic statistic) {
        ExtendedStatistics.Operation operation = (ExtendedStatistics.Operation)this.standardOperations.get((Object)statistic);
        if (operation instanceof NullCompoundOperation) {
            OperationStatistic discovered = ExtendedStatisticsImpl.findOperationStatistic(this.manager, statistic);
            if (discovered == null) {
                return operation;
            }
            CompoundOperationImpl<? extends Enum> newOperation = new CompoundOperationImpl<Enum>(discovered, statistic.type(), 1L, TimeUnit.SECONDS, this.executor, this.defaultHistorySize, statistic.isSearch() ? this.defaultSearchIntervalSeconds : this.defaultIntervalSeconds, TimeUnit.SECONDS);
            if (this.standardOperations.replace(statistic, operation, newOperation)) {
                return newOperation;
            }
            return (ExtendedStatistics.Operation)this.standardOperations.get((Object)statistic);
        }
        return operation;
    }

    private ExtendedStatistics.Statistic<Number> getStandardPassThrough(StandardPassThroughStatistic statistic) {
        ExtendedStatistics.Statistic passThrough = (ExtendedStatistics.Statistic)this.standardPassThroughs.get((Object)statistic);
        if (passThrough instanceof NullStatistic) {
            ValueStatistic discovered = ExtendedStatisticsImpl.findPassThroughStatistic(this.manager, statistic);
            if (discovered == null) {
                return passThrough;
            }
            SemiExpiringStatistic<Number> newPassThrough = new SemiExpiringStatistic<Number>(discovered, this.executor, this.defaultHistorySize, TimeUnit.SECONDS.toNanos(this.defaultIntervalSeconds));
            if (this.standardPassThroughs.replace(statistic, passThrough, newPassThrough)) {
                return newPassThrough;
            }
            return (ExtendedStatistics.Statistic)this.standardPassThroughs.get((Object)statistic);
        }
        return passThrough;
    }

    private static OperationStatistic findOperationStatistic(StatisticsManager manager, StandardOperationStatistic statistic) {
        Set<OperationStatistic<? extends Enum>> results = ExtendedStatisticsImpl.findOperationStatistic(manager, statistic.context(), statistic.type(), statistic.operationName(), statistic.tags());
        switch (results.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return results.iterator().next();
            }
        }
        throw new IllegalStateException("Duplicate statistics found for " + statistic);
    }

    private static ValueStatistic findPassThroughStatistic(StatisticsManager manager, StandardPassThroughStatistic statistic) {
        Set<ValueStatistic<?>> results = ExtendedStatisticsImpl.findPassThroughStatistic(manager, statistic.context(), statistic.statisticName(), statistic.tags());
        switch (results.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return results.iterator().next();
            }
        }
        throw new IllegalStateException("Duplicate statistics found for " + statistic);
    }

    private static <T extends Enum<T>> Set<OperationStatistic<T>> findOperationStatistic(StatisticsManager manager, Query contextQuery, Class<T> type, String name, final Set<String> tags) {
        Set<TreeNode> operationStatisticNodes = manager.query(QueryBuilder.queryBuilder().chain(contextQuery).children().filter(Matchers.context(Matchers.identifier(Matchers.subclassOf(OperationStatistic.class)))).build());
        Set<TreeNode> result = QueryBuilder.queryBuilder().filter(Matchers.context(Matchers.attributes(Matchers.allOf(Matchers.hasAttribute("type", type), Matchers.hasAttribute("name", name), Matchers.hasAttribute("tags", (Matcher<? extends Object>)new Matcher<Set<String>>(){

            @Override
            protected boolean matchesSafely(Set<String> object) {
                return object.containsAll(tags);
            }
        }))))).build().execute(operationStatisticNodes);
        if (result.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet<OperationStatistic<T>> statistics = new HashSet<OperationStatistic<T>>();
        for (TreeNode node : result) {
            statistics.add((OperationStatistic)node.getContext().attributes().get("this"));
        }
        return statistics;
    }

    private static Set<ValueStatistic<?>> findPassThroughStatistic(StatisticsManager manager, Query contextQuery, String name, final Set<String> tags) {
        Set<TreeNode> passThroughStatisticNodes = manager.query(QueryBuilder.queryBuilder().chain(contextQuery).children().filter(Matchers.context(Matchers.identifier(Matchers.subclassOf(ValueStatistic.class)))).build());
        Set<TreeNode> result = QueryBuilder.queryBuilder().filter(Matchers.context(Matchers.attributes(Matchers.allOf(Matchers.hasAttribute("name", name), Matchers.hasAttribute("tags", (Matcher<? extends Object>)new Matcher<Set<String>>(){

            @Override
            protected boolean matchesSafely(Set<String> object) {
                return object.containsAll(tags);
            }
        }))))).build().execute(passThroughStatisticNodes);
        if (result.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet statistics = new HashSet();
        for (TreeNode node : result) {
            statistics.add((ValueStatistic)node.getContext().attributes().get("this"));
        }
        return statistics;
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.ClusterEventOutcomes> clusterEvent() {
        return this.getStandardOperation(StandardOperationStatistic.CLUSTER_EVENT);
    }

    @Override
    public ExtendedStatistics.Operation<CacheOperationOutcomes.NonStopOperationOutcomes> nonstop() {
        return this.getStandardOperation(StandardOperationStatistic.NONSTOP);
    }

    @Override
    public ExtendedStatistics.Statistic<Number> mostRecentRejoinTimeStampMillis() {
        return this.getStandardPassThrough(StandardPassThroughStatistic.LAST_REJOIN_TIMESTAMP);
    }

    @Override
    public ExtendedStatistics.Statistic<Double> nonstopTimeoutRatio() {
        return this.nonStopTimeoutRatio;
    }

    public void dispose() {
        ScheduledFuture p = this.disableStatus;
        if (p != null) {
            p.cancel(true);
        }
    }
}

