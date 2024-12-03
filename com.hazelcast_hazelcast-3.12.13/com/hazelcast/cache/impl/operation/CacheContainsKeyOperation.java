/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;

public class CacheContainsKeyOperation
extends KeyBasedCacheOperation
implements ReadonlyOperation {
    public CacheContainsKeyOperation() {
    }

    public CacheContainsKeyOperation(String name, Data key) {
        super(name, key, true);
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore != null && this.recordStore.contains(this.key);
    }

    @Override
    public int getId() {
        return 2;
    }
}

