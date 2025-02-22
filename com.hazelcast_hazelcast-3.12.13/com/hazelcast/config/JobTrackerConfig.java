/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.JobTrackerConfigReadOnly;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.mapreduce.TopologyChangedStrategy;

public class JobTrackerConfig
implements NamedConfig {
    public static final int DEFAULT_MAX_THREAD_SIZE = RuntimeAvailableProcessors.get();
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_CHUNK_SIZE = 1000;
    public static final int DEFAULT_QUEUE_SIZE = 0;
    public static final boolean DEFAULT_COMMUNICATE_STATS = true;
    public static final TopologyChangedStrategy DEFAULT_TOPOLOGY_CHANGED_STRATEGY = TopologyChangedStrategy.CANCEL_RUNNING_OPERATION;
    private String name;
    private int maxThreadSize = DEFAULT_MAX_THREAD_SIZE;
    private int retryCount = 0;
    private int chunkSize = 1000;
    private int queueSize = 0;
    private boolean communicateStats = true;
    private TopologyChangedStrategy topologyChangedStrategy = DEFAULT_TOPOLOGY_CHANGED_STRATEGY;

    public JobTrackerConfig() {
    }

    public JobTrackerConfig(JobTrackerConfig source) {
        this.name = source.name;
        this.maxThreadSize = source.maxThreadSize;
        this.retryCount = source.retryCount;
        this.chunkSize = source.chunkSize;
        this.queueSize = source.queueSize;
        this.communicateStats = source.communicateStats;
        this.topologyChangedStrategy = source.topologyChangedStrategy;
    }

    @Override
    public JobTrackerConfig setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getMaxThreadSize() {
        return this.maxThreadSize;
    }

    public void setMaxThreadSize(int maxThreadSize) {
        this.maxThreadSize = maxThreadSize;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public JobTrackerConfig getAsReadOnly() {
        return new JobTrackerConfigReadOnly(this);
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public boolean isCommunicateStats() {
        return this.communicateStats;
    }

    public void setCommunicateStats(boolean communicateStats) {
        this.communicateStats = communicateStats;
    }

    public TopologyChangedStrategy getTopologyChangedStrategy() {
        return this.topologyChangedStrategy;
    }

    public void setTopologyChangedStrategy(TopologyChangedStrategy topologyChangedStrategy) {
        this.topologyChangedStrategy = topologyChangedStrategy;
    }
}

