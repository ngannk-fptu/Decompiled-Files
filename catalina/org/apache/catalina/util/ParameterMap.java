/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

public final class ParameterMap<K, V>
implements Map<K, V>,
Serializable {
    private static final long serialVersionUID = 2L;
    private final Map<K, V> delegatedMap;
    private final Map<K, V> unmodifiableDelegatedMap;
    private boolean locked = false;
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.util");

    public ParameterMap() {
        this.delegatedMap = new LinkedHashMap();
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public ParameterMap(int initialCapacity) {
        this.delegatedMap = new LinkedHashMap(initialCapacity);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public ParameterMap(int initialCapacity, float loadFactor) {
        this.delegatedMap = new LinkedHashMap(initialCapacity, loadFactor);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public ParameterMap(Map<K, V> map) {
        this.delegatedMap = new LinkedHashMap<K, V>(map);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void clear() {
        this.checkLocked();
        this.delegatedMap.clear();
    }

    @Override
    public V put(K key, V value) {
        this.checkLocked();
        return this.delegatedMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        this.checkLocked();
        this.delegatedMap.putAll(map);
    }

    @Override
    public V remove(Object key) {
        this.checkLocked();
        return this.delegatedMap.remove(key);
    }

    private void checkLocked() {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("parameterMap.locked"));
        }
    }

    @Override
    public int size() {
        return this.delegatedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegatedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegatedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegatedMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.delegatedMap.get(key);
    }

    @Override
    public Set<K> keySet() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.keySet();
        }
        return this.delegatedMap.keySet();
    }

    @Override
    public Collection<V> values() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.values();
        }
        return this.delegatedMap.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.entrySet();
        }
        return this.delegatedMap.entrySet();
    }
}

