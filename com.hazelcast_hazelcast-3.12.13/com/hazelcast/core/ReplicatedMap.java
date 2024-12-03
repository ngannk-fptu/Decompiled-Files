/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryListener;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.query.Predicate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface ReplicatedMap<K, V>
extends Map<K, V>,
DistributedObject {
    public V put(K var1, V var2, long var3, TimeUnit var5);

    @Override
    public void clear();

    public boolean removeEntryListener(String var1);

    public String addEntryListener(EntryListener<K, V> var1);

    public String addEntryListener(EntryListener<K, V> var1, K var2);

    public String addEntryListener(EntryListener<K, V> var1, Predicate<K, V> var2);

    public String addEntryListener(EntryListener<K, V> var1, Predicate<K, V> var2, K var3);

    @Override
    public Collection<V> values();

    public Collection<V> values(Comparator<V> var1);

    @Override
    public Set<Map.Entry<K, V>> entrySet();

    @Override
    public Set<K> keySet();

    public LocalReplicatedMapStats getReplicatedMapStats();
}

