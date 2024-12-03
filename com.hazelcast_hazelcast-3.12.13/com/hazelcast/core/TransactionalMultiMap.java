/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.BaseMultiMap;
import com.hazelcast.transaction.TransactionalObject;
import java.util.Collection;

public interface TransactionalMultiMap<K, V>
extends BaseMultiMap<K, V>,
TransactionalObject {
    @Override
    public boolean put(K var1, V var2);

    @Override
    public Collection<V> get(K var1);

    @Override
    public boolean remove(Object var1, Object var2);

    @Override
    public Collection<V> remove(Object var1);

    @Override
    public int valueCount(K var1);

    @Override
    public int size();
}

