/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.PartitionWideCacheOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class CacheSizeOperation
extends PartitionWideCacheOperation
implements ReadonlyOperation {
    public CacheSizeOperation() {
    }

    public CacheSizeOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        ICacheRecordStore cache = service.getRecordStore(this.name, this.getPartitionId());
        this.response = cache != null ? cache.size() : 0;
    }

    @Override
    public int getId() {
        return 13;
    }
}

