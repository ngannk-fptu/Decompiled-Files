/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.strategy;

import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.SerializableByConvention;

@SerializableByConvention
public class StringPartitioningStrategy
implements PartitioningStrategy {
    public static final StringPartitioningStrategy INSTANCE = new StringPartitioningStrategy();

    public Object getPartitionKey(Object key) {
        if (key instanceof String) {
            return StringPartitioningStrategy.getPartitionKey((String)key);
        }
        return null;
    }

    public static String getBaseName(String name) {
        if (name == null) {
            return null;
        }
        int indexOf = name.indexOf(64);
        if (indexOf == -1) {
            return name;
        }
        return name.substring(0, indexOf);
    }

    public static String getPartitionKey(String key) {
        if (key == null) {
            return null;
        }
        int firstIndexOf = key.indexOf(64);
        if (firstIndexOf == -1) {
            return key;
        }
        return key.substring(firstIndexOf + 1);
    }
}

