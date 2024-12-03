/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

class ReplicatedMapConfigReadOnly
extends ReplicatedMapConfig {
    public ReplicatedMapConfigReadOnly(ReplicatedMapConfig replicatedMapConfig) {
        super(replicatedMapConfig);
    }

    @Override
    public ReplicatedMapConfig setReplicatorExecutorService(ScheduledExecutorService replicatorExecutorService) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setName(String name) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setReplicationDelayMillis(long replicationDelayMillis) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setConcurrencyLevel(int concurrencyLevel) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setListenerConfigs(List<ListenerConfig> listenerConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public void setAsyncFillup(boolean asyncFillup) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setQuorumName(String quorumName) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setMergePolicy(String mergePolicy) {
        throw this.throwReadOnly();
    }

    @Override
    public ReplicatedMapConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        throw this.throwReadOnly();
    }

    private UnsupportedOperationException throwReadOnly() {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

