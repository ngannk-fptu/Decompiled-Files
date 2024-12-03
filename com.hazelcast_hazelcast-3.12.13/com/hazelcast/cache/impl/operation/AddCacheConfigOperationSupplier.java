/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.cache.impl.operation.AddCacheConfigOperation;
import com.hazelcast.util.function.Supplier;

public class AddCacheConfigOperationSupplier
implements Supplier<AddCacheConfigOperation> {
    private final PreJoinCacheConfig cacheConfig;

    public AddCacheConfigOperationSupplier(PreJoinCacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    @Override
    public AddCacheConfigOperation get() {
        return new AddCacheConfigOperation(this.cacheConfig);
    }
}

