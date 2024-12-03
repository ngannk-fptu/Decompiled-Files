/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.cachingtier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.AbstractCacheConfigurationListener;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.SizeOfEngineLoader;
import net.sf.ehcache.pool.sizeof.annotations.IgnoreSizeOf;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.CachingTier;
import net.sf.ehcache.store.FifoPolicy;
import net.sf.ehcache.store.LfuPolicy;
import net.sf.ehcache.store.LruPolicy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.store.cachingtier.CountBasedBackEnd;
import net.sf.ehcache.store.cachingtier.HeapCacheBackEnd;
import net.sf.ehcache.store.cachingtier.PooledBasedBackEnd;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.terracotta.context.annotations.ContextChild;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.observer.OperationObserver;

public class OnHeapCachingTier<K, V>
implements CachingTier<K, V> {
    @ContextChild
    private final HeapCacheBackEnd<K, Object> backEnd;
    private final OperationObserver<StoreOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.GetOutcome.class).named("get")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<StoreOperationOutcomes.PutOutcome> putObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.PutOutcome.class).named("put")).of(this)).tag(new String[]{"local-heap"})).build();
    private final OperationObserver<StoreOperationOutcomes.RemoveOutcome> removeObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(StoreOperationOutcomes.RemoveOutcome.class).named("remove")).of(this)).tag(new String[]{"local-heap"})).build();
    private volatile List<CachingTier.Listener<K, V>> listeners = new CopyOnWriteArrayList<CachingTier.Listener<K, V>>();

    public OnHeapCachingTier(HeapCacheBackEnd<K, Object> backEnd) {
        this.backEnd = backEnd;
        this.backEnd.registerEvictionCallback(new HeapCacheBackEnd.EvictionCallback<K, Object>(){

            @Override
            public void evicted(K key, Object value) {
                Object v = OnHeapCachingTier.this.getValue(value);
                if (v != null) {
                    for (CachingTier.Listener listener : OnHeapCachingTier.this.listeners) {
                        listener.evicted(key, v);
                    }
                }
            }
        });
    }

    public static OnHeapCachingTier<Object, Element> createOnHeapCache(Ehcache cache, Pool onHeapPool) {
        ConcurrentHashMap memCacheBackEnd;
        Policy memoryEvictionPolicy = OnHeapCachingTier.determineEvictionPolicy(cache);
        if (cache.getCacheConfiguration().isCountBasedTuned()) {
            CountBasedBackEnd countBasedBackEnd;
            long maxEntriesLocalHeap = OnHeapCachingTier.getCachingTierMaxEntryCount(cache);
            memCacheBackEnd = countBasedBackEnd = new CountBasedBackEnd(maxEntriesLocalHeap, memoryEvictionPolicy);
            cache.getCacheConfiguration().addConfigurationListener(new AbstractCacheConfigurationListener(){

                @Override
                public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
                    countBasedBackEnd.setMaxEntriesLocalHeap(newCapacity);
                }
            });
        } else {
            PooledBasedBackEnd<Object, Object> pooledBasedBackEnd = new PooledBasedBackEnd<Object, Object>(memoryEvictionPolicy);
            pooledBasedBackEnd.registerAccessor(onHeapPool.createPoolAccessor(new PooledBasedBackEnd.PoolParticipant(pooledBasedBackEnd), SizeOfPolicyConfiguration.resolveMaxDepth(cache), SizeOfPolicyConfiguration.resolveBehavior(cache).equals((Object)SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT)));
            memCacheBackEnd = pooledBasedBackEnd;
        }
        return new OnHeapCachingTier<Object, Element>((HeapCacheBackEnd<Object, Object>)((Object)memCacheBackEnd));
    }

    static Policy determineEvictionPolicy(Ehcache cache) {
        MemoryStoreEvictionPolicy policySelection = cache.getCacheConfiguration().getMemoryStoreEvictionPolicy();
        if (policySelection.equals(MemoryStoreEvictionPolicy.LRU)) {
            return new LruPolicy();
        }
        if (policySelection.equals(MemoryStoreEvictionPolicy.FIFO)) {
            return new FifoPolicy();
        }
        if (policySelection.equals(MemoryStoreEvictionPolicy.LFU)) {
            return new LfuPolicy();
        }
        if (policySelection.equals(MemoryStoreEvictionPolicy.CLOCK)) {
            return new LruPolicy();
        }
        throw new IllegalArgumentException(policySelection + " isn't a valid eviction policy");
    }

    private static long getCachingTierMaxEntryCount(Ehcache cache) {
        PinningConfiguration pinningConfiguration = cache.getCacheConfiguration().getPinningConfiguration();
        if (pinningConfiguration != null && pinningConfiguration.getStore() != PinningConfiguration.Store.INCACHE) {
            return 0L;
        }
        return cache.getCacheConfiguration().getMaxEntriesLocalHeap();
    }

    @Override
    public boolean loadOnPut() {
        return this.backEnd.hasSpace();
    }

    @Override
    public V get(K key, Callable<V> source, boolean updateStats) {
        Fault<V> cachedValue;
        if (updateStats) {
            this.getObserver.begin();
        }
        if ((cachedValue = this.backEnd.get(key)) == null) {
            Fault<V> f;
            if (updateStats) {
                this.getObserver.end(StoreOperationOutcomes.GetOutcome.MISS);
            }
            if ((cachedValue = this.backEnd.putIfAbsent(key, f = new Fault<V>(source))) == null) {
                try {
                    V value = f.get();
                    this.putObserver.begin();
                    if (value == null) {
                        this.backEnd.remove(key, f);
                    } else if (this.backEnd.replace(key, f, value)) {
                        this.putObserver.end(StoreOperationOutcomes.PutOutcome.ADDED);
                    } else {
                        V p = this.getValue(this.backEnd.remove(key));
                        return p == null ? value : p;
                    }
                    return value;
                }
                catch (Throwable e) {
                    this.backEnd.remove(key, f);
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException)e;
                    }
                    throw new CacheException(e);
                }
            }
        } else if (updateStats) {
            this.getObserver.end(StoreOperationOutcomes.GetOutcome.HIT);
        }
        return this.getValue(cachedValue);
    }

    @Override
    public V remove(K key) {
        this.removeObserver.begin();
        try {
            V v = this.getValue(this.backEnd.remove(key));
            return v;
        }
        finally {
            this.removeObserver.end(StoreOperationOutcomes.RemoveOutcome.SUCCESS);
        }
    }

    @Override
    public void clear() {
        this.backEnd.clear(false);
    }

    @Override
    public void clearAndNotify() {
        this.backEnd.clear(true);
    }

    @Override
    public void addListener(CachingTier.Listener<K, V> listener) {
        if (listener == null) {
            throw new NullPointerException("Listener can't be null!");
        }
        this.listeners.add(listener);
    }

    @Override
    @Statistic(name="size", tags={"local-heap"})
    public int getInMemorySize() {
        return this.backEnd.size();
    }

    @Override
    public int getOffHeapSize() {
        return 0;
    }

    @Override
    public boolean contains(K key) {
        return this.backEnd.get(key) != null;
    }

    @Override
    @Statistic(name="size-in-bytes", tags={"local-heap"})
    public long getInMemorySizeInBytes() {
        long sizeInBytes;
        if (this.backEnd instanceof PooledBasedBackEnd) {
            sizeInBytes = ((PooledBasedBackEnd)this.backEnd).getSizeInBytes();
        } else {
            SizeOfEngine defaultSizeOfEngine = SizeOfEngineLoader.newSizeOfEngine(1000, SizeOfPolicyConfiguration.DEFAULT_MAX_DEPTH_EXCEEDED_BEHAVIOR == SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT, true);
            sizeInBytes = 0L;
            for (Map.Entry<K, Object> entry : this.backEnd.entrySet()) {
                if (entry.getValue() == null || !(entry.getValue() instanceof Element)) continue;
                Element element = (Element)entry.getValue();
                Size size = defaultSizeOfEngine.sizeOf(element.getObjectKey(), element, null);
                sizeInBytes += size.getCalculated();
            }
        }
        return sizeInBytes;
    }

    @Override
    public long getOffHeapSizeInBytes() {
        return 0L;
    }

    @Override
    public long getOnDiskSizeInBytes() {
        return 0L;
    }

    @Override
    public void recalculateSize(K key) {
        this.backEnd.recalculateSize(key);
    }

    @Override
    public Policy getEvictionPolicy() {
        return this.backEnd.getPolicy();
    }

    @Override
    public void setEvictionPolicy(Policy policy) {
        this.backEnd.setPolicy(policy);
    }

    private V getValue(Object cachedValue) {
        if (cachedValue instanceof Fault) {
            return ((Fault)cachedValue).get();
        }
        return (V)cachedValue;
    }

    @IgnoreSizeOf
    private static class Fault<V> {
        private final Callable<V> source;
        private V value;
        private Throwable throwable;
        private boolean complete;

        public Fault(Callable<V> source) {
            this.source = source;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void complete(V value) {
            Fault fault = this;
            synchronized (fault) {
                this.value = value;
                this.complete = true;
                this.notifyAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private V get() {
            Fault fault = this;
            synchronized (fault) {
                if (!this.complete) {
                    try {
                        this.complete(this.source.call());
                    }
                    catch (Throwable e) {
                        this.fail(e);
                    }
                }
            }
            return this.throwOrReturn();
        }

        private V throwOrReturn() {
            if (this.throwable != null) {
                if (this.throwable instanceof RuntimeException) {
                    throw (RuntimeException)this.throwable;
                }
                throw new CacheException("Faulting from repository failed", this.throwable);
            }
            return this.value;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void fail(Throwable t) {
            Fault fault = this;
            synchronized (fault) {
                this.throwable = t;
                this.complete = true;
                this.notifyAll();
            }
            this.throwOrReturn();
        }
    }
}

