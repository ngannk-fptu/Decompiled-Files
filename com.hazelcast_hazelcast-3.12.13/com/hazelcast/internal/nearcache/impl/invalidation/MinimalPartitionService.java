/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.nio.serialization.Data;

public interface MinimalPartitionService {
    public int getPartitionId(Data var1);

    public int getPartitionId(Object var1);

    public int getPartitionCount();
}

