/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import java.time.Duration;
import java.util.function.Supplier;

public class UnversionedExternalCacheRequestContext<V>
extends AbstractExternalCacheRequestContext<V> {
    private static final long FIXED_CACHE_VERSION = 200106074L;

    public UnversionedExternalCacheRequestContext(ExternalCacheKeyGenerator keyGenerator, String name, Supplier<String> partitionSupplier, Duration lockTimeout) {
        super(keyGenerator, name, partitionSupplier, lockTimeout);
    }

    @Override
    protected final long cacheVersion() {
        return 200106074L;
    }
}

