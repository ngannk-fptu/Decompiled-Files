/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.FlakeIdGeneratorConfig;

public class FlakeIdGeneratorConfigReadOnly
extends FlakeIdGeneratorConfig {
    FlakeIdGeneratorConfigReadOnly(FlakeIdGeneratorConfig config) {
        super(config);
    }

    @Override
    public void setName(String name) {
        throw this.throwReadOnly();
    }

    @Override
    public FlakeIdGeneratorConfig setPrefetchCount(int prefetchCount) {
        throw this.throwReadOnly();
    }

    @Override
    public FlakeIdGeneratorConfig setPrefetchValidityMillis(long prefetchValidityMs) {
        throw this.throwReadOnly();
    }

    @Override
    public FlakeIdGeneratorConfig setIdOffset(long idOffset) {
        throw this.throwReadOnly();
    }

    @Override
    public FlakeIdGeneratorConfig setNodeIdOffset(long nodeIdOffset) {
        throw this.throwReadOnly();
    }

    @Override
    public FlakeIdGeneratorConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw this.throwReadOnly();
    }

    private UnsupportedOperationException throwReadOnly() {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

