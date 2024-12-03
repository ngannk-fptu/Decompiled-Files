/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.util.internal;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;

public class MapBasedDictionary<K, V>
extends Dictionary<K, V>
implements Map<K, V> {
    private Map<K, V> map;

    public MapBasedDictionary(Map<K, V> map) {
        this.map = map == null ? new LinkedHashMap() : map;
    }

    public MapBasedDictionary() {
        this.map = new LinkedHashMap();
    }

    public MapBasedDictionary(int initialCapacity) {
        this.map = new LinkedHashMap(initialCapacity);
    }

    public MapBasedDictionary(Dictionary<? extends K, ? extends V> dictionary) {
        this(new LinkedHashMap(), dictionary);
    }

    public MapBasedDictionary(Map<K, V> map, Dictionary<? extends K, ? extends V> dictionary) {
        this(map);
        if (dictionary != null) {
            this.putAll(dictionary);
        }
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        this.map.putAll(t);
    }

    @Override
    public <T extends K> void putAll(Dictionary<T, ? extends V> dictionary) {
        if (dictionary != null) {
            Enumeration<T> enm = dictionary.keys();
            while (enm.hasMoreElements()) {
                T key = enm.nextElement();
                this.map.put(key, dictionary.get(key));
            }
        }
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.map.remove(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    public Enumeration<V> elements() {
        return new IteratorBasedEnumeration<V>(this.map.values());
    }

    @Override
    public Enumeration<K> keys() {
        return new IteratorBasedEnumeration<K>(this.map.keySet());
    }

    public String toString() {
        return this.map.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.map.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    private static class IteratorBasedEnumeration<E>
    implements Enumeration<E> {
        private Iterator<E> it;

        public IteratorBasedEnumeration(Iterator<E> it) {
            Assert.notNull(it);
            this.it = it;
        }

        public IteratorBasedEnumeration(Collection<E> col) {
            this(col.iterator());
        }

        @Override
        public boolean hasMoreElements() {
            return this.it.hasNext();
        }

        @Override
        public E nextElement() {
            return this.it.next();
        }
    }
}

