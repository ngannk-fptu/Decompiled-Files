/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainerCollector;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceProxy;
import com.hazelcast.concurrent.atomicreference.operations.AtomicReferenceReplicationOperation;
import com.hazelcast.concurrent.atomicreference.operations.MergeOperation;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.nio.serialization.Data;
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

public class AtomicReferenceService
implements ManagedService,
RemoteService,
MigrationAwareService,
QuorumAwareService,
SplitBrainHandlerService {
    public static final String SERVICE_NAME = "hz:impl:atomicReferenceService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, AtomicReferenceContainer> containers = new ConcurrentHashMap<String, AtomicReferenceContainer>();
    private final ConstructorFunction<String, AtomicReferenceContainer> atomicReferenceConstructorFunction = new ConstructorFunction<String, AtomicReferenceContainer>(){

        @Override
        public AtomicReferenceContainer createNew(String key) {
            return new AtomicReferenceContainer();
        }
    };
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            AtomicReferenceConfig config = AtomicReferenceService.this.nodeEngine.getConfig().findAtomicReferenceConfig(name);
            String quorumName = config.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private NodeEngine nodeEngine;

    public AtomicReferenceContainer getReferenceContainer(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.containers, name, this.atomicReferenceConstructorFunction);
    }

    public boolean containsReferenceContainer(String name) {
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
    public AtomicReferenceProxy createDistributedObject(String name) {
        AtomicReferenceConfig atomicReferenceConfig = this.nodeEngine.getConfig().findAtomicReferenceConfig(name);
        ConfigValidator.checkBasicConfig(atomicReferenceConfig, this.nodeEngine.getSplitBrainMergePolicyProvider());
        return new AtomicReferenceProxy(name, this.nodeEngine, this);
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
        HashMap<String, Data> data = new HashMap<String, Data>();
        int partitionId = event.getPartitionId();
        for (Map.Entry containerEntry : this.containers.entrySet()) {
            String name = (String)containerEntry.getKey();
            if (partitionId != this.getPartitionId(name)) continue;
            AtomicReferenceContainer atomicReferenceContainer = (AtomicReferenceContainer)containerEntry.getValue();
            Data value = atomicReferenceContainer.get();
            data.put(name, value);
        }
        return data.isEmpty() ? null : new AtomicReferenceReplicationOperation(data);
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

    private int getPartitionId(String name) {
        IPartitionService partitionService = this.nodeEngine.getPartitionService();
        String partitionKey = StringPartitioningStrategy.getPartitionKey(name);
        return partitionService.getPartitionId(partitionKey);
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    @Override
    public Runnable prepareMergeRunnable() {
        AtomicReferenceContainerCollector collector = new AtomicReferenceContainerCollector(this.nodeEngine, this.containers);
        collector.run();
        return new Merger(collector);
    }

    private class Merger
    extends AbstractContainerMerger<AtomicReferenceContainer, Object, SplitBrainMergeTypes.AtomicReferenceMergeTypes> {
        Merger(AtomicReferenceContainerCollector collector) {
            super(collector, AtomicReferenceService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "AtomicReference";
        }

        @Override
        public void runInternal() {
            AtomicReferenceContainerCollector collector = (AtomicReferenceContainerCollector)this.collector;
            for (Map.Entry entry : collector.getCollectedContainers().entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containerList = (Collection)entry.getValue();
                for (AtomicReferenceContainer container : containerList) {
                    String name = collector.getContainerName(container);
                    SplitBrainMergePolicy<Object, SplitBrainMergeTypes.AtomicReferenceMergeTypes> mergePolicy = this.getMergePolicy(collector.getMergePolicyConfig(container));
                    MergeOperation operation = new MergeOperation(name, mergePolicy, container.get());
                    this.invoke(AtomicReferenceService.SERVICE_NAME, operation, partitionId);
                }
            }
        }
    }
}

