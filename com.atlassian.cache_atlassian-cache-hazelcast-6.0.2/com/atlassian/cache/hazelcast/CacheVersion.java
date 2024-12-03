/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.IAtomicLong
 */
package com.atlassian.cache.hazelcast;

import com.hazelcast.core.IAtomicLong;
import java.util.Objects;

class CacheVersion {
    private final IAtomicLong version;

    public CacheVersion(IAtomicLong version) {
        this.version = Objects.requireNonNull(version);
    }

    public long get() {
        return this.version.get();
    }

    public long incrementAndGet() {
        return this.version.incrementAndGet();
    }
}

