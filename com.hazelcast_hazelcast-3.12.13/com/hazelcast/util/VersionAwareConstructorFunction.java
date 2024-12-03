/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.version.Version;

public interface VersionAwareConstructorFunction<K, V>
extends ConstructorFunction<K, V> {
    public V createNew(K var1, Version var2);
}

