/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.comparators;

import com.hazelcast.spi.serialization.SerializationService;

public interface ValueComparator {
    public boolean isEqual(Object var1, Object var2, SerializationService var3);
}

