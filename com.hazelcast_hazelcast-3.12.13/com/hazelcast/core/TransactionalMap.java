/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.BaseMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.transaction.TransactionalObject;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface TransactionalMap<K, V>
extends TransactionalObject,
BaseMap<K, V> {
    @Override
    public boolean containsKey(Object var1);

    @Override
    public V get(Object var1);

    public V getForUpdate(Object var1);

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public V put(K var1, V var2);

    @Override
    public V put(K var1, V var2, long var3, TimeUnit var5);

    @Override
    public void set(K var1, V var2);

    @Override
    public V putIfAbsent(K var1, V var2);

    @Override
    public V replace(K var1, V var2);

    @Override
    public boolean replace(K var1, V var2, V var3);

    @Override
    public V remove(Object var1);

    @Override
    public void delete(Object var1);

    @Override
    public boolean remove(Object var1, Object var2);

    @Override
    public Set<K> keySet();

    @Override
    public Set<K> keySet(Predicate var1);

    @Override
    public Collection<V> values();

    @Override
    public Collection<V> values(Predicate var1);
}

