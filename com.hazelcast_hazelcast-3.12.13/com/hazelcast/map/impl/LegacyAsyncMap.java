/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.BaseMap;
import com.hazelcast.map.EntryProcessor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface LegacyAsyncMap<K, V>
extends BaseMap<K, V> {
    public Future<V> getAsync(K var1);

    public Future<V> putAsync(K var1, V var2);

    public Future<V> putAsync(K var1, V var2, long var3, TimeUnit var5);

    public Future<Void> setAsync(K var1, V var2);

    public Future<Void> setAsync(K var1, V var2, long var3, TimeUnit var5);

    public Future<V> removeAsync(K var1);

    public Future submitToKey(K var1, EntryProcessor var2);
}

