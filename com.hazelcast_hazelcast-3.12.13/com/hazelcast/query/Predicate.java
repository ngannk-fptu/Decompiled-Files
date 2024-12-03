/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;
import java.util.Map;

@BinaryInterface
public interface Predicate<K, V>
extends Serializable {
    public boolean apply(Map.Entry<K, V> var1);
}

