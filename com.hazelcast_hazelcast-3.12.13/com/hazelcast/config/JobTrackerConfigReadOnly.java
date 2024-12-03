/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.mapreduce.TopologyChangedStrategy;

public class JobTrackerConfigReadOnly
extends JobTrackerConfig {
    JobTrackerConfigReadOnly(JobTrackerConfig jobTrackerConfig) {
        super(jobTrackerConfig);
    }

    @Override
    public JobTrackerConfigReadOnly setName(String name) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setMaxThreadSize(int maxThreadSize) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setRetryCount(int retryCount) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setChunkSize(int chunkSize) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setQueueSize(int queueSize) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setCommunicateStats(boolean communicateStats) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setTopologyChangedStrategy(TopologyChangedStrategy topologyChangedStrategy) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

