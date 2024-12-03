/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.map.listener.MapListener;
import com.hazelcast.query.Predicate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface QueryCache<K, V> {
    public V get(Object var1);

    public boolean containsKey(Object var1);

    public boolean containsValue(Object var1);

    public boolean isEmpty();

    public int size();

    public void addIndex(String var1, boolean var2);

    public Map<K, V> getAll(Set<K> var1);

    public Set<K> keySet();

    public Set<K> keySet(Predicate var1);

    public Set<Map.Entry<K, V>> entrySet();

    public Set<Map.Entry<K, V>> entrySet(Predicate var1);

    public Collection<V> values();

    public Collection<V> values(Predicate var1);

    public String addEntryListener(MapListener var1, boolean var2);

    public String addEntryListener(MapListener var1, K var2, boolean var3);

    public String addEntryListener(MapListener var1, Predicate<K, V> var2, boolean var3);

    public String addEntryListener(MapListener var1, Predicate<K, V> var2, K var3, boolean var4);

    public boolean removeEntryListener(String var1);

    public String getName();

    public boolean tryRecover();

    public void destroy();
}

