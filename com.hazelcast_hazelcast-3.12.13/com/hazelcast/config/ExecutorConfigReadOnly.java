/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ExecutorConfig;

public class ExecutorConfigReadOnly
extends ExecutorConfig {
    public ExecutorConfigReadOnly(ExecutorConfig config) {
        super(config);
    }

    @Override
    public ExecutorConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only executor: " + this.getName());
    }

    @Override
    public ExecutorConfig setPoolSize(int poolSize) {
        throw new UnsupportedOperationException("This config is read-only executor: " + this.getName());
    }

    @Override
    public ExecutorConfig setQueueCapacity(int queueCapacity) {
        throw new UnsupportedOperationException("This config is read-only executor: " + this.getName());
    }

    @Override
    public ExecutorConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only executor: " + this.getName());
    }

    @Override
    public ExecutorConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only executor: " + this.getName());
    }
}

