/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store.cachingtier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.LruPolicy;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.cachingtier.HeapCacheBackEnd;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountBasedBackEnd<K, V>
extends ConcurrentHashMap<K, V>
implements HeapCacheBackEnd<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger((String)CountBasedBackEnd.class.getName());
    private static final int MAX_EVICTIONS = 5;
    private static final int SAMPLING_SIZE = 30;
    private volatile long maxEntriesLocalHeap;
    private volatile Policy policy;
    private volatile ConcurrentHashMap.RemovalCallback callback;

    public CountBasedBackEnd(long maxEntriesLocalHeap) {
        this(maxEntriesLocalHeap, new LruPolicy());
    }

    public CountBasedBackEnd(long maxEntriesLocalHeap, Policy policy) {
        this.maxEntriesLocalHeap = maxEntriesLocalHeap;
        this.setPolicy(policy);
    }

    @Override
    public void setPolicy(Policy policy) {
        if (policy == null) {
            throw new NullPointerException("We need a Policy passed in here, null won't cut it!");
        }
        this.policy = policy;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V v = super.putIfAbsent(key, value);
        if (v == null) {
            try {
                this.evictIfRequired(key, value);
            }
            catch (Throwable e) {
                LOG.warn("Caught throwable while evicting", e);
            }
        }
        return v;
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
    public void recalculateSize(K key) {
    }

    @Override
    public V remove(Object key) {
        return super.removeAndNotify(key, this.callback);
    }

    @Override
    public Policy getPolicy() {
        return this.policy;
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
        return this.maxEntriesLocalHeap == 0L || this.maxEntriesLocalHeap > this.mappingCount();
    }

    private void evictIfRequired(K key, V value) {
        if (this.maxEntriesLocalHeap == 0L) {
            return;
        }
        int evictions = 5;
        while (this.maxEntriesLocalHeap < this.mappingCount() && evictions-- > 0) {
            Element evictionCandidate = this.findEvictionCandidate(key, value);
            if (evictionCandidate == null) continue;
            this.remove(evictionCandidate.getObjectKey(), evictionCandidate, this.callback);
        }
    }

    private Element findEvictionCandidate(K key, V value) {
        List values = this.getRandomValues(30);
        ArrayList<Element> elements = new ArrayList<Element>(values.size() * 2);
        for (Object v : values) {
            if (!(v instanceof Element) || ((Element)v).getObjectKey().equals(key)) continue;
            elements.add((Element)v);
        }
        return this.policy.selectedBasedOnPolicy(elements.toArray(new Element[elements.size()]), value instanceof Element ? (Element)value : null);
    }

    public void setMaxEntriesLocalHeap(long maxEntriesLocalHeap) {
        this.maxEntriesLocalHeap = maxEntriesLocalHeap;
    }

    public long getMaxEntriesLocalHeap() {
        return this.maxEntriesLocalHeap;
    }
}

