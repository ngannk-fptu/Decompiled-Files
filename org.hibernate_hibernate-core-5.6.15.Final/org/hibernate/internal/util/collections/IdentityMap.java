/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class IdentityMap<K, V>
implements Map<K, V> {
    private final LinkedHashMap<IdentityKey<K>, V> map;
    private transient Map.Entry<IdentityKey<K>, V>[] entryArray = null;

    public static <K, V> IdentityMap<K, V> instantiateSequenced(int size) {
        return new IdentityMap(new LinkedHashMap(size << 1, 0.6f));
    }

    private IdentityMap(LinkedHashMap<IdentityKey<K>, V> underlyingMap) {
        this.map = underlyingMap;
    }

    public static <K, V> Map.Entry<K, V>[] concurrentEntries(Map<K, V> map) {
        return ((IdentityMap)map).entryArray();
    }

    public static <K, V> void onEachKey(Map<K, V> map, Consumer<K> consumer) {
        IdentityMap identityMap = (IdentityMap)map;
        identityMap.map.forEach((kIdentityKey, v) -> consumer.accept(((IdentityKey)kIdentityKey).key));
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.map.forEach((k, v) -> action.accept((Object)((IdentityKey)k).key, (Object)v));
    }

    public Iterator<K> keyIterator() {
        return new KeyIterator(this.map.keySet().iterator());
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(new IdentityKey<Object>(key));
    }

    @Override
    public boolean containsValue(Object val) {
        throw new UnsupportedOperationException("Avoid this operation: does not perform well");
    }

    @Override
    public V get(Object key) {
        return this.map.get(new IdentityKey<Object>(key));
    }

    @Override
    public V put(K key, V value) {
        this.entryArray = null;
        return this.map.put(new IdentityKey<K>(key), value);
    }

    @Override
    public V remove(Object key) {
        this.entryArray = null;
        return this.map.remove(new IdentityKey<Object>(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> otherMap) {
        for (Map.Entry<K, V> entry : otherMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.entryArray = null;
        this.map.clear();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Map.Entry<K, V>> set = new HashSet<Map.Entry<K, V>>(this.map.size());
        for (Map.Entry<IdentityKey<K>, V> entry : this.map.entrySet()) {
            set.add(new IdentityMapEntry<Object, V>(((IdentityKey)entry.getKey()).key, entry.getValue()));
        }
        return set;
    }

    public Map.Entry[] entryArray() {
        if (this.entryArray == null) {
            this.entryArray = new Map.Entry[this.map.size()];
            Iterator<Map.Entry<IdentityKey<K>, V>> itr = this.map.entrySet().iterator();
            int i = 0;
            while (itr.hasNext()) {
                Map.Entry<IdentityKey<K>, V> me = itr.next();
                this.entryArray[i++] = new IdentityMapEntry<Object, V>(((IdentityKey)me.getKey()).key, me.getValue());
            }
        }
        return this.entryArray;
    }

    public String toString() {
        return this.map.toString();
    }

    private static final class IdentityKey<K>
    implements Serializable {
        private final K key;

        IdentityKey(K key) {
            this.key = key;
        }

        public boolean equals(Object other) {
            return other != null && this.key == ((IdentityKey)other).key;
        }

        public int hashCode() {
            return System.identityHashCode(this.key);
        }

        public String toString() {
            return this.key.toString();
        }
    }

    private static final class IdentityMapEntry<K, V>
    implements Map.Entry<K, V> {
        private final K key;
        private final V value;

        IdentityMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class KeyIterator<K>
    implements Iterator<K> {
        private final Iterator<IdentityKey<K>> identityKeyIterator;

        private KeyIterator(Iterator<IdentityKey<K>> iterator) {
            this.identityKeyIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.identityKeyIterator.hasNext();
        }

        @Override
        public K next() {
            return (K)((IdentityKey)this.identityKeyIterator.next()).key;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

