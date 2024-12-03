/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

abstract class AbstractDecoratedMap<K, V>
extends AbstractMap<K, V>
implements Map<K, V>,
Serializable,
Cloneable {
    protected Map<K, Map.Entry<K, V>> entries;
    protected volatile transient int modCount;
    private volatile transient Set<Map.Entry<K, V>> entrySet = null;
    private volatile transient Set<K> keySet = null;
    private volatile transient Collection<V> values = null;

    public AbstractDecoratedMap() {
        this(new HashMap(), null);
    }

    public AbstractDecoratedMap(Map<? extends K, ? extends V> map) {
        this(new HashMap(), map);
    }

    public AbstractDecoratedMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2) {
        if (map == null) {
            throw new IllegalArgumentException("backing == null");
        }
        Map.Entry[] entryArray = null;
        if (map == map2) {
            Map.Entry[] entryArray2 = map2.entrySet();
            entryArray = new Map.Entry[entryArray2.size()];
            entryArray = entryArray2.toArray(entryArray);
            map2 = null;
            map.clear();
        } else if (!map.isEmpty()) {
            throw new IllegalArgumentException("backing must be empty");
        }
        this.entries = map;
        this.init();
        if (map2 != null) {
            this.putAll(map2);
        } else if (entryArray != null) {
            for (Map.Entry entry : entryArray) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    protected void init() {
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public void clear() {
        this.entries.clear();
        ++this.modCount;
        this.init();
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object object) {
        return this.entries.containsKey(object);
    }

    @Override
    public boolean containsValue(Object object) {
        for (V v : this.values()) {
            if (v != object && (v == null || !v.equals(object))) continue;
            return true;
        }
        return false;
    }

    @Override
    public Collection<V> values() {
        Values values = this.values;
        return values != null ? values : (this.values = new Values());
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet entrySet = this.entrySet;
        return entrySet != null ? entrySet : (this.entrySet = new EntrySet());
    }

    @Override
    public Set<K> keySet() {
        KeySet keySet = this.keySet;
        return keySet != null ? keySet : (this.keySet = new KeySet());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractDecoratedMap abstractDecoratedMap = (AbstractDecoratedMap)super.clone();
        abstractDecoratedMap.values = null;
        abstractDecoratedMap.entrySet = null;
        abstractDecoratedMap.keySet = null;
        return abstractDecoratedMap;
    }

    protected abstract Iterator<K> newKeyIterator();

    protected abstract Iterator<V> newValueIterator();

    protected abstract Iterator<Map.Entry<K, V>> newEntryIterator();

    @Override
    public abstract V get(Object var1);

    @Override
    public abstract V remove(Object var1);

    @Override
    public abstract V put(K var1, V var2);

    Map.Entry<K, V> createEntry(K k, V v) {
        return new BasicEntry<K, V>(k, v);
    }

    Map.Entry<K, V> getEntry(K k) {
        return this.entries.get(k);
    }

    protected Map.Entry<K, V> removeEntry(Map.Entry<K, V> entry) {
        if (entry == null) {
            return null;
        }
        Map.Entry<K, V> entry2 = this.getEntry(entry.getKey());
        if (entry2 == entry || entry2 != null && entry2.equals(entry)) {
            this.remove(entry.getKey());
            return entry;
        }
        return null;
    }

    static class BasicEntry<K, V>
    implements Map.Entry<K, V>,
    Serializable {
        K mKey;
        V mValue;

        BasicEntry(K k, V v) {
            this.mKey = k;
            this.mValue = v;
        }

        protected void recordAccess(Map<K, V> map) {
        }

        protected void recordRemoval(Map<K, V> map) {
        }

        @Override
        public V getValue() {
            return this.mValue;
        }

        @Override
        public V setValue(V v) {
            V v2 = this.mValue;
            this.mValue = v;
            return v2;
        }

        @Override
        public K getKey() {
            return this.mKey;
        }

        @Override
        public boolean equals(Object object) {
            Object v;
            V v2;
            if (!(object instanceof Map.Entry)) {
                return false;
            }
            K k = this.mKey;
            Map.Entry entry = (Map.Entry)object;
            Object k2 = entry.getKey();
            return (k == k2 || k != null && k.equals(k2)) && ((v2 = this.mValue) == (v = entry.getValue()) || v2 != null && v2.equals(v));
        }

        @Override
        public int hashCode() {
            return (this.mKey == null ? 0 : this.mKey.hashCode()) ^ (this.mValue == null ? 0 : this.mValue.hashCode());
        }

        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }

    protected class KeySet
    extends AbstractSet<K> {
        protected KeySet() {
        }

        @Override
        public Iterator<K> iterator() {
            return AbstractDecoratedMap.this.newKeyIterator();
        }

        @Override
        public int size() {
            return AbstractDecoratedMap.this.size();
        }

        @Override
        public boolean contains(Object object) {
            return AbstractDecoratedMap.this.containsKey(object);
        }

        @Override
        public boolean remove(Object object) {
            return AbstractDecoratedMap.this.remove(object) != null;
        }

        @Override
        public void clear() {
            AbstractDecoratedMap.this.clear();
        }
    }

    protected class EntrySet
    extends AbstractSet<Map.Entry<K, V>> {
        protected EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return AbstractDecoratedMap.this.newEntryIterator();
        }

        @Override
        public boolean contains(Object object) {
            if (!(object instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)object;
            Map.Entry entry2 = AbstractDecoratedMap.this.entries.get(entry.getKey());
            return entry2 != null && entry2.equals(entry);
        }

        @Override
        public boolean remove(Object object) {
            if (!(object instanceof Map.Entry)) {
                return false;
            }
            return AbstractDecoratedMap.this.removeEntry((Map.Entry)object) != null;
        }

        @Override
        public int size() {
            return AbstractDecoratedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractDecoratedMap.this.clear();
        }
    }

    protected class Values
    extends AbstractCollection<V> {
        protected Values() {
        }

        @Override
        public Iterator<V> iterator() {
            return AbstractDecoratedMap.this.newValueIterator();
        }

        @Override
        public int size() {
            return AbstractDecoratedMap.this.size();
        }

        @Override
        public boolean contains(Object object) {
            return AbstractDecoratedMap.this.containsValue(object);
        }

        @Override
        public void clear() {
            AbstractDecoratedMap.this.clear();
        }
    }
}

