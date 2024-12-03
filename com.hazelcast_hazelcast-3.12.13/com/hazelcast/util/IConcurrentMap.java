/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.core.IBiFunction;
import com.hazelcast.core.IFunction;
import java.util.concurrent.ConcurrentMap;

public interface IConcurrentMap<K, V>
extends ConcurrentMap<K, V> {
    public V applyIfAbsent(K var1, IFunction<? super K, ? extends V> var2);

    public V applyIfPresent(K var1, IBiFunction<? super K, ? super V, ? extends V> var2);
}

