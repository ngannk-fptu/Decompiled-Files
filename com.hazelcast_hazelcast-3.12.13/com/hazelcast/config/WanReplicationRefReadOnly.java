/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.util.List;

@BinaryInterface
public class WanReplicationRefReadOnly
extends WanReplicationRef {
    public WanReplicationRefReadOnly(WanReplicationRef ref) {
        super(ref);
    }

    @Override
    public WanReplicationRef setName(String name) {
        throw this.throwReadOnly();
    }

    @Override
    public WanReplicationRef setMergePolicy(String mergePolicy) {
        throw this.throwReadOnly();
    }

    @Override
    public WanReplicationRef setFilters(List<String> filters) {
        throw this.throwReadOnly();
    }

    @Override
    public WanReplicationRef addFilter(String filter) {
        throw this.throwReadOnly();
    }

    @Override
    public WanReplicationRef setRepublishingEnabled(boolean republishingEnabled) {
        throw this.throwReadOnly();
    }

    private UnsupportedOperationException throwReadOnly() {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

