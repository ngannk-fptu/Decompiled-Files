/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Lazy
 *  javax.annotation.Nullable
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import io.atlassian.util.concurrent.Lazy;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class VersionedExternalCacheRequestContext<V>
extends AbstractExternalCacheRequestContext<V> {
    protected final String externalCacheVersionKey;
    private final Supplier<Long> cacheVersionSupplier;
    @Nullable
    private Long cacheVersion;

    public VersionedExternalCacheRequestContext(ExternalCacheKeyGenerator keyGenerator, String name, Supplier<String> partitionSupplier, Function<String, Long> cacheVersionSupplier, Duration lockTimeout) {
        super(keyGenerator, name, partitionSupplier, lockTimeout);
        this.externalCacheVersionKey = keyGenerator.cacheVersionKey(partitionSupplier.get(), name);
        this.cacheVersionSupplier = Lazy.supplier(() -> (Long)cacheVersionSupplier.apply(this.externalCacheVersionKey));
    }

    @Override
    protected long cacheVersion() {
        return this.cacheVersion == null ? this.cacheVersionSupplier.get() : this.cacheVersion;
    }

    public void updateCacheVersion(Function<String, Long> cacheVersionSupplier) {
        this.cacheVersion = cacheVersionSupplier.apply(this.externalCacheVersionKey);
        this.clearKeyMaps();
    }
}

