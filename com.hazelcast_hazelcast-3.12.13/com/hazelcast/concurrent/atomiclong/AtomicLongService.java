/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.AtomicLongContainerCollector;
import com.hazelcast.concurrent.atomiclong.AtomicLongProxy;
import com.hazelcast.concurrent.atomiclong.operations.AtomicLongReplicationOperation;
import com.hazelcast.concurrent.atomiclong.operations.MergeOperation;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.impl.merge.AbstractContainerMerger;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AtomicLongService
implements ManagedService,
RemoteService,
MigrationAwareService,
QuorumAwareService,
SplitBrainHandlerService {
    public static final String SERVICE_NAME = "hz:impl:atomicLongService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, AtomicLongContainer> containers = new ConcurrentHashMap<String, AtomicLongContainer>();
    private final ConstructorFunction<String, AtomicLongContainer> atomicLongConstructorFunction = new ConstructorFunction<String, AtomicLongContainer>(){

        @Override
        public AtomicLongContainer createNew(String key) {
            return new AtomicLongContainer();
        }
    };
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            AtomicLongConfig config = AtomicLongService.this.nodeEngine.getConfig().findAtomicLongConfig(name);
            String quorumName = config.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private NodeEngine nodeEngine;

    public AtomicLongContainer getLongContainer(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.containers, name, this.atomicLongConstructorFunction);
    }

    public boolean containsAtomicLong(String name) {
        return this.containers.containsKey(name);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void reset() {
        this.containers.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    @Override
    public AtomicLongProxy createDistributedObject(String name) {
        AtomicLongConfig atomicLongConfig = this.nodeEngine.getConfig().findAtomicLongConfig(name);
        ConfigValidator.checkBasicConfig(atomicLongConfig, this.nodeEngine.getSplitBrainMergePolicyProvider());
        return new AtomicLongProxy(name, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.containers.remove(name);
        this.quorumConfigCache.remove(name);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        if (event.getReplicaIndex() > 1) {
            return null;
        }
        HashMap<String, Long> data = new HashMap<String, Long>();
        int partitionId = event.getPartitionId();
        for (Map.Entry containerEntry : this.containers.entrySet()) {
            String name = (String)containerEntry.getKey();
            if (partitionId != this.getPartitionId(name)) continue;
            AtomicLongContainer container = (AtomicLongContainer)containerEntry.getValue();
            data.put(name, container.get());
        }
        return data.isEmpty() ? null : new AtomicLongReplicationOperation(data);
    }

    private int getPartitionId(String name) {
        IPartitionService partitionService = this.nodeEngine.getPartitionService();
        String partitionKey = StringPartitioningStrategy.getPartitionKey(name);
        return partitionService.getPartitionId(partitionKey);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        int thresholdReplicaIndex;
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE && ((thresholdReplicaIndex = event.getNewReplicaIndex()) == -1 || thresholdReplicaIndex > 1)) {
            this.clearPartitionReplica(event.getPartitionId());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        int thresholdReplicaIndex;
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION && ((thresholdReplicaIndex = event.getCurrentReplicaIndex()) == -1 || thresholdReplicaIndex > 1)) {
            this.clearPartitionReplica(event.getPartitionId());
        }
    }

    private void clearPartitionReplica(int partitionId) {
        Iterator iterator = this.containers.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String)iterator.next();
            if (this.getPartitionId(name) != partitionId) continue;
            iterator.remove();
        }
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    @Override
    public Runnable prepareMergeRunnable() {
        AtomicLongContainerCollector collector = new AtomicLongContainerCollector(this.nodeEngine, this.containers);
        collector.run();
        return new Merger(collector);
    }

    private class Merger
    extends AbstractContainerMerger<AtomicLongContainer, Long, SplitBrainMergeTypes.AtomicLongMergeTypes> {
        Merger(AtomicLongContainerCollector collector) {
            super(collector, AtomicLongService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "AtomicLong";
        }

        @Override
        public void runInternal() {
            AtomicLongContainerCollector collector = (AtomicLongContainerCollector)this.collector;
            for (Map.Entry entry : collector.getCollectedContainers().entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containerList = (Collection)entry.getValue();
                for (AtomicLongContainer container : containerList) {
                    String name = collector.getContainerName(container);
                    SplitBrainMergePolicy<Long, SplitBrainMergeTypes.AtomicLongMergeTypes> mergePolicy = this.getMergePolicy(collector.getMergePolicyConfig(container));
                    MergeOperation operation = new MergeOperation(name, mergePolicy, container.get());
                    this.invoke(AtomicLongService.SERVICE_NAME, operation, partitionId);
                }
            }
        }
    }
}

