/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;
import java.util.Map;

@BinaryInterface
public interface EntryProcessor<K, V>
extends Serializable {
    public Object process(Map.Entry<K, V> var1);

    public EntryBackupProcessor<K, V> getBackupProcessor();
}

