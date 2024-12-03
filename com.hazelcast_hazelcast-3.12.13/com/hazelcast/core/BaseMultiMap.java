/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import java.util.Collection;

public interface BaseMultiMap<K, V>
extends DistributedObject {
    public boolean put(K var1, V var2);

    public Collection<V> get(K var1);

    public boolean remove(Object var1, Object var2);

    public Collection<V> remove(Object var1);

    public int valueCount(K var1);

    public int size();
}

