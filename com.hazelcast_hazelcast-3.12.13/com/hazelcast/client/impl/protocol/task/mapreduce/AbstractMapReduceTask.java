/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.operation.KeyValueJobOperation;
import com.hazelcast.mapreduce.impl.operation.StartProcessingJobOperation;
import com.hazelcast.mapreduce.impl.task.TrackableJobFuture;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.StringUtil;
import java.security.Permission;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractMapReduceTask<Parameters>
extends AbstractMessageTask<Parameters>
implements ExecutionCallback,
BlockingMessageTask {
    public AbstractMapReduceTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        TrackableJobFuture jobFuture;
        MapReduceService mapReduceService = (MapReduceService)this.getService("hz:impl:mapReduceService");
        NodeEngine nodeEngine = mapReduceService.getNodeEngine();
        ClusterService clusterService = nodeEngine.getClusterService();
        if (clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR) == 0) {
            throw new IllegalStateException("Could not register map reduce job since there are no nodes owning a partition");
        }
        String objectName = this.getDistributedObjectName();
        AbstractJobTracker jobTracker = (AbstractJobTracker)mapReduceService.createDistributedObject(objectName);
        if (jobTracker.registerTrackableJob(jobFuture = new TrackableJobFuture(objectName, this.getJobId(), jobTracker, nodeEngine, null))) {
            this.startSupervisionTask(jobTracker);
            jobFuture.andThen(this);
        }
    }

    protected abstract String getJobId();

    protected abstract int getChunkSize();

    protected abstract String getTopologyChangedStrategy();

    protected abstract KeyValueSource getKeyValueSource();

    protected abstract Mapper getMapper();

    protected abstract CombinerFactory getCombinerFactory();

    protected abstract ReducerFactory getReducerFactory();

    protected abstract Collection getKeys();

    protected abstract KeyPredicate getPredicate();

    private void startSupervisionTask(JobTracker jobTracker) {
        Operation operation;
        MapReduceService mapReduceService = (MapReduceService)this.getService("hz:impl:mapReduceService");
        JobTrackerConfig config = ((AbstractJobTracker)jobTracker).getJobTrackerConfig();
        boolean communicateStats = config.isCommunicateStats();
        int chunkSize = this.getChunkSizeOrConfigChunkSize(config);
        TopologyChangedStrategy topologyChangedStrategy = this.getTopologyChangedStrategyOrConfigTopologyChangedStrategy(config);
        String name = this.getDistributedObjectName();
        String jobId = this.getJobId();
        KeyValueSource keyValueSource = this.getKeyValueSource();
        Mapper mapper = this.getMapper();
        CombinerFactory combinerFactory = this.getCombinerFactory();
        ReducerFactory reducerFactory = this.getReducerFactory();
        Collection keys = this.getKeys();
        Collection<Object> keyObjects = this.getKeyObjects(keys);
        KeyPredicate predicate = this.getPredicate();
        ClusterService clusterService = this.nodeEngine.getClusterService();
        for (Member member : clusterService.getMembers(KeyValueJobOperation.MEMBER_SELECTOR)) {
            operation = new KeyValueJobOperation(name, jobId, chunkSize, keyValueSource, mapper, combinerFactory, reducerFactory, communicateStats, topologyChangedStrategy);
            MapReduceUtil.executeOperation(operation, member.getAddress(), mapReduceService, (NodeEngine)this.nodeEngine);
        }
        for (Member member : clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR)) {
            operation = new StartProcessingJobOperation<Object>(name, jobId, keyObjects, predicate);
            MapReduceUtil.executeOperation(operation, member.getAddress(), mapReduceService, (NodeEngine)this.nodeEngine);
        }
    }

    private int getChunkSizeOrConfigChunkSize(JobTrackerConfig config) {
        int chunkSize = this.getChunkSize();
        if (chunkSize == -1) {
            chunkSize = config.getChunkSize();
        }
        return chunkSize;
    }

    private TopologyChangedStrategy getTopologyChangedStrategyOrConfigTopologyChangedStrategy(JobTrackerConfig config) {
        String topologyChangedStrategyStr = this.getTopologyChangedStrategy();
        TopologyChangedStrategy topologyChangedStrategy = topologyChangedStrategyStr == null ? config.getTopologyChangedStrategy() : TopologyChangedStrategy.valueOf(topologyChangedStrategyStr.toUpperCase(StringUtil.LOCALE_INTERNAL));
        return topologyChangedStrategy;
    }

    private Collection<Object> getKeyObjects(Collection keys) {
        ArrayList keyObjects = null;
        if (keys != null) {
            keyObjects = new ArrayList(keys.size());
            for (Object key : keys) {
                keyObjects.add(this.serializationService.toObject(key));
            }
        }
        return keyObjects;
    }

    public void onResponse(Object response) {
        Map m = (Map)response;
        ArrayList entries = new ArrayList();
        for (Map.Entry entry : m.entrySet()) {
            Object key = this.serializationService.toData(entry.getKey());
            Object value = this.serializationService.toData(entry.getValue());
            entries.add(new AbstractMap.SimpleEntry(key, value));
        }
        this.sendResponse(entries);
    }

    @Override
    public void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapReduceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

