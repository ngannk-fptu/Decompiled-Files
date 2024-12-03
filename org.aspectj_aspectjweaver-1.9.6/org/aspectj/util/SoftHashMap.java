/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoftHashMap<K, V>
extends AbstractMap<K, V> {
    private Map<K, SpecialValue> map;
    private ReferenceQueue<? super V> rq = new ReferenceQueue();

    public SoftHashMap() {
        this.map = new HashMap<K, SpecialValue>();
    }

    private void processQueue() {
        SpecialValue sv = null;
        while ((sv = (SpecialValue)this.rq.poll()) != null) {
            this.map.remove(sv.key);
        }
    }

    @Override
    public V get(Object key) {
        SpecialValue ref = this.map.get(key);
        if (ref == null) {
            this.map.remove(key);
            return null;
        }
        Object value = ref.get();
        if (value == null) {
            this.map.remove(ref.key);
            return null;
        }
        return (V)value;
    }

    @Override
    public V put(K k, V v) {
        this.processQueue();
        SpecialValue sv = new SpecialValue(k, v);
        SpecialValue result = this.map.put(k, sv);
        return result == null ? null : (V)result.get();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.map.isEmpty()) {
            return Collections.emptyMap().entrySet();
        }
        HashMap currentContents = new HashMap();
        for (Map.Entry<K, SpecialValue> entry : this.map.entrySet()) {
            Object currentValueForEntry = entry.getValue().get();
            if (currentValueForEntry == null) continue;
            currentContents.put(entry.getKey(), currentValueForEntry);
        }
        return currentContents.entrySet();
    }

    @Override
    public void clear() {
        this.processQueue();
        this.map.clear();
    }

    @Override
    public int size() {
        this.processQueue();
        return this.map.size();
    }

    @Override
    public V remove(Object k) {
        this.processQueue();
        SpecialValue ref = this.map.remove(k);
        if (ref == null) {
            return null;
        }
        return (V)ref.get();
    }

    class SpecialValue
    extends SoftReference<V> {
        private final K key;

        SpecialValue(K k, V v) {
            super(v, SoftHashMap.this.rq);
            this.key = k;
        }
    }
}

