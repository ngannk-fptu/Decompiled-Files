/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import java.util.Map;

public interface Registry<K, T> {
    public T getOrCreate(K var1);

    public T getOrNull(K var1);

    public Map<K, T> getAll();

    public T remove(K var1);
}

