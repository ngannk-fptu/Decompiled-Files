/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store.cachingtier;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.sf.ehcache.Element;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.store.cachingtier.CountBasedBackEnd;
import net.sf.ehcache.store.cachingtier.HeapCacheBackEnd;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.derived.EventRateSimpleMovingAverage;
import org.terracotta.statistics.derived.OperationResultFilter;
import org.terracotta.statistics.observer.OperationObserver;

public class PooledBasedBackEnd<K, V>
extends ConcurrentHashMap<K, V>
implements HeapCacheBackEnd<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger((String)CountBasedBackEnd.class.getName());
    private static final int MAX_EVICTIONS = 5;
    private static final float PUT_LOAD_THRESHOLD = 0.9f;
    private volatile Policy policy;
    private volatile ConcurrentHashMap.RemovalCallback callback;
    private final AtomicReference<PoolAccessor> poolAccessor = new AtomicReference();
    private final OperationObserver<StoreOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.GetOutcome.class).named("arc-get")).of(this)).tag(new String[]{"private"})).build();

    public PooledBasedBackEnd(Policy memoryEvictionPolicy) {
        this.setPolicy(memoryEvictionPolicy);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        long delta = this.poolAccessor.get().add(key, value, FAKE_TREE_NODE, false);
        if (delta > -1L) {
            Object previous = super.internalPutIfAbsent(key, value, delta > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)delta);
            if (previous != null) {
                this.poolAccessor.get().delete(delta);
            }
            return (V)previous;
        }
        ConcurrentHashMap.RemovalCallback cb = this.callback;
        if (cb != null) {
            cb.removed(key, value);
        }
        return null;
    }

    @Override
    public V get(Object key) {
        this.getObserver.begin();
        Object value = super.get(key);
        if (value != null) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.HIT);
        } else {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        return super.removeAndNotify(key, this.callback);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return super.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(boolean notify) {
        if (notify) {
            for (Map.Entry entry : this.entrySet()) {
                if (!(entry.getValue() instanceof Element)) continue;
                this.removeAndNotify(entry.getKey(), entry.getValue(), this.callback);
            }
        } else {
            super.clear();
        }
    }

    @Override
    public boolean hasSpace() {
        PoolAccessor accessor = this.poolAccessor.get();
        return (float)accessor.getPoolOccupancy() < 0.9f * (float)accessor.getPoolSize();
    }

    public boolean evict(int evictions) {
        while (evictions-- > 0) {
            Element evictionCandidate = this.findEvictionCandidate();
            if (evictionCandidate != null) {
                this.remove(evictionCandidate.getObjectKey(), evictionCandidate, this.callback);
                continue;
            }
            return false;
        }
        return true;
    }

    private Element findEvictionCandidate() {
        List values = this.getRandomValues(5);
        ArrayList<Element> elements = new ArrayList<Element>(values.size() * 2);
        for (Object v : values) {
            if (!(v instanceof Element)) continue;
            elements.add((Element)v);
        }
        return this.policy.selectedBasedOnPolicy(elements.toArray(new Element[elements.size()]), null);
    }

    @Override
    public void setPolicy(Policy policy) {
        if (policy == null) {
            throw new NullPointerException("We need a Policy passed in here, null won't cut it!");
        }
        this.policy = policy;
    }

    @Override
    public void registerEvictionCallback(final HeapCacheBackEnd.EvictionCallback<K, V> evictionCallback) {
        this.callback = evictionCallback == null ? null : new ConcurrentHashMap.RemovalCallback(){

            @Override
            public void removed(Object key, Object value) {
                evictionCallback.evicted(key, value);
            }
        };
    }

    @Override
    public Policy getPolicy() {
        return this.policy;
    }

    public void registerAccessor(PoolAccessor poolAccessor) {
        if (poolAccessor == null) {
            throw new NullPointerException("No null poolAccessor allowed here!");
        }
        if (!this.poolAccessor.compareAndSet(null, poolAccessor)) {
            throw new IllegalStateException("Can't set the poolAccessor multiple times!");
        }
        super.setPoolAccessor(poolAccessor);
    }

    @Deprecated
    public long getSizeInBytes() {
        return this.poolAccessor.get().getSize();
    }

    public static class PoolParticipant
    implements net.sf.ehcache.pool.PoolParticipant {
        private final EventRateSimpleMovingAverage hitRate = new EventRateSimpleMovingAverage(1L, TimeUnit.SECONDS);
        private final EventRateSimpleMovingAverage missRate = new EventRateSimpleMovingAverage(1L, TimeUnit.SECONDS);
        private final PooledBasedBackEnd<Object, Object> pooledBasedBackEnd;

        public PoolParticipant(PooledBasedBackEnd<Object, Object> pooledBasedBackEnd) {
            this.pooledBasedBackEnd = pooledBasedBackEnd;
            OperationStatistic<StoreOperationOutcomes.GetOutcome> getStatistic = StatisticsManager.getOperationStatisticFor(pooledBasedBackEnd.getObserver);
            getStatistic.addDerivedStatistic((StoreOperationOutcomes.GetOutcome)((Object)new OperationResultFilter<StoreOperationOutcomes.GetOutcome>(EnumSet.of(StoreOperationOutcomes.GetOutcome.HIT), this.hitRate)));
            getStatistic.addDerivedStatistic((StoreOperationOutcomes.GetOutcome)((Object)new OperationResultFilter<StoreOperationOutcomes.GetOutcome>(EnumSet.of(StoreOperationOutcomes.GetOutcome.MISS), this.missRate)));
        }

        @Override
        public boolean evict(int count, long size) {
            try {
                return this.pooledBasedBackEnd.evict(count);
            }
            catch (Throwable e) {
                LOG.warn("Caught throwable while evicting", e);
                return false;
            }
        }

        @Override
        public float getApproximateHitRate() {
            return this.hitRate.rateUsingSeconds().floatValue();
        }

        @Override
        public float getApproximateMissRate() {
            return this.missRate.rateUsingSeconds().floatValue();
        }

        @Override
        public long getApproximateCountSize() {
            return this.pooledBasedBackEnd.mappingCount();
        }
    }
}

