/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LinkedHashtable<K, V>
extends Hashtable<K, V> {
    private static final long serialVersionUID = 1L;
    private final LinkedHashMap<K, V> map;

    public LinkedHashtable() {
        this.map = new LinkedHashMap();
    }

    public LinkedHashtable(int initialCapacity) {
        this.map = new LinkedHashMap(initialCapacity);
    }

    public LinkedHashtable(int initialCapacity, float loadFactor) {
        this.map = new LinkedHashMap(initialCapacity, loadFactor);
    }

    public LinkedHashtable(Map<K, V> m) {
        this.map = new LinkedHashMap<K, V>(m);
    }

    @Override
    public synchronized void clear() {
        this.map.clear();
    }

    @Override
    public boolean contains(Object value) {
        return this.containsKey(value);
    }

    @Override
    public synchronized boolean containsKey(Object value) {
        return this.map.containsKey(value);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Enumeration<V> elements() {
        return Collections.enumeration(this.values());
    }

    @Override
    public synchronized Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public synchronized boolean equals(Object o) {
        return this.map.equals(o);
    }

    @Override
    public synchronized V get(Object k) {
        return this.map.get(k);
    }

    @Override
    public synchronized int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Enumeration<K> keys() {
        return Collections.enumeration(this.keySet());
    }

    @Override
    public synchronized Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public synchronized V put(K k, V v) {
        return this.map.put(k, v);
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        this.map.putAll(m);
    }

    @Override
    public synchronized V remove(Object k) {
        return this.map.remove(k);
    }

    @Override
    public synchronized int size() {
        return this.map.size();
    }

    @Override
    public synchronized String toString() {
        return this.map.toString();
    }

    @Override
    public synchronized Collection<V> values() {
        return this.map.values();
    }
}

