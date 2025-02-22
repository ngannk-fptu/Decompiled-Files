/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;

public class JobTaskConfiguration {
    private final Address jobOwner;
    private final int chunkSize;
    private final String name;
    private final String jobId;
    private final Mapper mapper;
    private final CombinerFactory combinerFactory;
    private final ReducerFactory reducerFactory;
    private final KeyValueSource keyValueSource;
    private final NodeEngine nodeEngine;
    private final boolean communicateStats;
    private final TopologyChangedStrategy topologyChangedStrategy;

    public JobTaskConfiguration(Address jobOwner, NodeEngine nodeEngine, int chunkSize, String name, String jobId, Mapper mapper, CombinerFactory combinerFactory, ReducerFactory reducerFactory, KeyValueSource keyValueSource, boolean communicateStats, TopologyChangedStrategy topologyChangedStrategy) {
        this.jobOwner = jobOwner;
        this.chunkSize = chunkSize;
        this.name = name;
        this.jobId = jobId;
        this.mapper = mapper;
        this.combinerFactory = combinerFactory;
        this.reducerFactory = reducerFactory;
        this.keyValueSource = keyValueSource;
        this.nodeEngine = nodeEngine;
        this.communicateStats = communicateStats;
        this.topologyChangedStrategy = topologyChangedStrategy;
    }

    public Address getJobOwner() {
        return this.jobOwner;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    public String getName() {
        return this.name;
    }

    public String getJobId() {
        return this.jobId;
    }

    public Mapper getMapper() {
        return this.mapper;
    }

    public CombinerFactory getCombinerFactory() {
        return this.combinerFactory;
    }

    public ReducerFactory getReducerFactory() {
        return this.reducerFactory;
    }

    public KeyValueSource getKeyValueSource() {
        return this.keyValueSource;
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public boolean isCommunicateStats() {
        return this.communicateStats;
    }

    public TopologyChangedStrategy getTopologyChangedStrategy() {
        return this.topologyChangedStrategy;
    }
}

