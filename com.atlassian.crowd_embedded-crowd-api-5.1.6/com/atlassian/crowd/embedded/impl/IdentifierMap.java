/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IdentifierMap<V>
extends ForwardingMap<String, V> {
    private final HashMap<String, V> delegate;

    public IdentifierMap() {
        this.delegate = Maps.newHashMap();
    }

    public IdentifierMap(int expectedSize) {
        this.delegate = Maps.newHashMapWithExpectedSize((int)expectedSize);
    }

    public IdentifierMap(Map<String, V> map) {
        this(map.size());
        this.putAll(map);
    }

    protected Map<String, V> delegate() {
        return this.delegate;
    }

    private Object lowercase(Object key) {
        return key instanceof String ? IdentifierUtils.toLowerCase((String)key) : key;
    }

    public V remove(Object key) {
        return this.delegate().remove(this.lowercase(key));
    }

    public boolean containsKey(Object key) {
        return this.delegate().containsKey(this.lowercase(key));
    }

    public V get(Object key) {
        return this.delegate().get(this.lowercase(key));
    }

    public V put(String key, V value) {
        return this.delegate().put(IdentifierUtils.toLowerCase(key), value);
    }

    public void putAll(Map<? extends String, ? extends V> map) {
        this.delegate().putAll(IdentifierMap.lowercaseMap(map));
    }

    private static <V> Map<? extends String, ? extends V> lowercaseMap(Map<? extends String, ? extends V> map) {
        if (map instanceof IdentifierMap) {
            return map;
        }
        HashMap lowercaseMap = Maps.newHashMapWithExpectedSize((int)map.size());
        for (Map.Entry<String, V> entry : map.entrySet()) {
            lowercaseMap.put(IdentifierUtils.toLowerCase(entry.getKey()), entry.getValue());
        }
        return lowercaseMap;
    }

    public static <T> IdentifierMap<T> index(Collection<? extends T> items, Function<T, String> nameGetter) {
        IdentifierMap result = new IdentifierMap(items.size());
        items.forEach(item -> result.put((String)nameGetter.apply(item), item));
        return result;
    }
}

