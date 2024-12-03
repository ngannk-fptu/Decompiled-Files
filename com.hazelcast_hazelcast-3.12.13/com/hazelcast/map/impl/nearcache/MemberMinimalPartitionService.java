/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.nearcache;

import com.hazelcast.internal.nearcache.impl.invalidation.MinimalPartitionService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.partition.IPartitionService;

public class MemberMinimalPartitionService
implements MinimalPartitionService {
    private final IPartitionService partitionService;

    public MemberMinimalPartitionService(IPartitionService partitionService) {
        this.partitionService = partitionService;
    }

    @Override
    public int getPartitionId(Data key) {
        return this.partitionService.getPartitionId(key);
    }

    @Override
    public int getPartitionId(Object key) {
        return this.partitionService.getPartitionId(key);
    }

    @Override
    public int getPartitionCount() {
        return this.partitionService.getPartitionCount();
    }
}

