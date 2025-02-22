/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.impl.AbstractJob;
import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.operation.KeyValueJobOperation;
import com.hazelcast.mapreduce.impl.operation.StartProcessingJobOperation;
import com.hazelcast.mapreduce.impl.task.TrackableJobFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.UuidUtil;

public class KeyValueJob<KeyIn, ValueIn>
extends AbstractJob<KeyIn, ValueIn> {
    private final NodeEngine nodeEngine;
    private final MapReduceService mapReduceService;

    public KeyValueJob(String name, AbstractJobTracker jobTracker, NodeEngine nodeEngine, MapReduceService mapReduceService, KeyValueSource<KeyIn, ValueIn> keyValueSource) {
        super(name, jobTracker, keyValueSource);
        this.nodeEngine = nodeEngine;
        this.mapReduceService = mapReduceService;
    }

    @Override
    protected <T> JobCompletableFuture<T> invoke(Collator collator) {
        ClusterService clusterService = this.nodeEngine.getClusterService();
        if (clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR) == 0) {
            throw new IllegalStateException("Could not register map reduce job since there are no nodes owning a partition");
        }
        AbstractJobTracker jobTracker = (AbstractJobTracker)this.jobTracker;
        String jobId = UuidUtil.newUnsecureUuidString();
        TrackableJobFuture jobFuture = new TrackableJobFuture(this.name, jobId, jobTracker, this.nodeEngine, collator);
        if (jobTracker.registerTrackableJob(jobFuture)) {
            return this.startSupervisionTask(jobFuture, jobId);
        }
        throw new IllegalStateException("Could not register map reduce job");
    }

    private <T> JobCompletableFuture<T> startSupervisionTask(TrackableJobFuture<T> jobFuture, String jobId) {
        Operation operation;
        AbstractJobTracker jobTracker = (AbstractJobTracker)this.jobTracker;
        JobTrackerConfig config = jobTracker.getJobTrackerConfig();
        boolean communicateStats = config.isCommunicateStats();
        if (this.chunkSize == -1) {
            this.chunkSize = config.getChunkSize();
        }
        if (this.topologyChangedStrategy == null) {
            this.topologyChangedStrategy = config.getTopologyChangedStrategy();
        }
        ClusterService clusterService = this.nodeEngine.getClusterService();
        for (Member member : clusterService.getMembers(KeyValueJobOperation.MEMBER_SELECTOR)) {
            operation = new KeyValueJobOperation(this.name, jobId, this.chunkSize, this.keyValueSource, this.mapper, this.combinerFactory, this.reducerFactory, communicateStats, this.topologyChangedStrategy);
            MapReduceUtil.executeOperation(operation, member.getAddress(), this.mapReduceService, this.nodeEngine);
        }
        for (Member member : clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR)) {
            operation = new StartProcessingJobOperation(this.name, jobId, this.keys, this.predicate);
            MapReduceUtil.executeOperation(operation, member.getAddress(), this.mapReduceService, this.nodeEngine);
        }
        return jobFuture;
    }
}

