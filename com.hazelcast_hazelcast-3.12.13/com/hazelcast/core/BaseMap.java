/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.query.Predicate;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface BaseMap<K, V>
extends DistributedObject {
    public boolean containsKey(Object var1);

    public V get(Object var1);

    public V put(K var1, V var2);

    public V put(K var1, V var2, long var3, TimeUnit var5);

    public void set(K var1, V var2);

    public V putIfAbsent(K var1, V var2);

    public V replace(K var1, V var2);

    public boolean replace(K var1, V var2, V var3);

    public V remove(Object var1);

    public void delete(Object var1);

    public boolean remove(Object var1, Object var2);

    public boolean isEmpty();

    public int size();

    public Set<K> keySet();

    public Set<K> keySet(Predicate var1);

    public Collection<V> values();

    public Collection<V> values(Predicate var1);
}

