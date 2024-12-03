/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorContainer;
import com.hazelcast.cardinality.impl.CardinalityEstimatorContainerCollector;
import com.hazelcast.cardinality.impl.CardinalityEstimatorProxy;
import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.cardinality.impl.operations.MergeOperation;
import com.hazelcast.cardinality.impl.operations.ReplicationOperation;
import com.hazelcast.config.CardinalityEstimatorConfig;
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
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CardinalityEstimatorService
implements ManagedService,
RemoteService,
MigrationAwareService,
QuorumAwareService,
SplitBrainHandlerService {
    public static final String SERVICE_NAME = "hz:impl:cardinalityEstimatorService";
    private static final double SIZING_FUDGE_FACTOR = 1.3;
    private static final Object NULL_OBJECT = new Object();
    private NodeEngine nodeEngine;
    private final ConcurrentMap<String, CardinalityEstimatorContainer> containers = new ConcurrentHashMap<String, CardinalityEstimatorContainer>();
    private final ConstructorFunction<String, CardinalityEstimatorContainer> cardinalityEstimatorContainerConstructorFunction = new ConstructorFunction<String, CardinalityEstimatorContainer>(){

        @Override
        public CardinalityEstimatorContainer createNew(String name) {
            CardinalityEstimatorConfig config = CardinalityEstimatorService.this.nodeEngine.getConfig().findCardinalityEstimatorConfig(name);
            return new CardinalityEstimatorContainer(config.getBackupCount(), config.getAsyncBackupCount());
        }
    };
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            CardinalityEstimatorConfig config = CardinalityEstimatorService.this.nodeEngine.getConfig().findCardinalityEstimatorConfig(name);
            String quorumName = config.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };

    public void addCardinalityEstimator(String name, CardinalityEstimatorContainer container) {
        Preconditions.checkNotNull(name, "Name can't be null");
        Preconditions.checkNotNull(container, "Container can't be null");
        this.containers.put(name, container);
    }

    public CardinalityEstimatorContainer getCardinalityEstimatorContainer(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.containers, name, this.cardinalityEstimatorContainerConstructorFunction);
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
    public CardinalityEstimatorProxy createDistributedObject(String objectName) {
        return new CardinalityEstimatorProxy(objectName, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        this.containers.remove(objectName);
        this.quorumConfigCache.remove(objectName);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        IPartitionService partitionService = this.nodeEngine.getPartitionService();
        int roughSize = (int)((double)this.containers.size() * 1.3 / (double)partitionService.getPartitionCount());
        Map<String, CardinalityEstimatorContainer> data = MapUtil.createHashMap(roughSize);
        int partitionId = event.getPartitionId();
        for (Map.Entry containerEntry : this.containers.entrySet()) {
            String name = (String)containerEntry.getKey();
            CardinalityEstimatorContainer container = (CardinalityEstimatorContainer)containerEntry.getValue();
            if (partitionId != this.getPartitionId(name) || event.getReplicaIndex() > container.getTotalBackupCount()) continue;
            data.put(name, (CardinalityEstimatorContainer)containerEntry.getValue());
        }
        return data.isEmpty() ? null : new ReplicationOperation(data);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearPartitionReplica(event.getPartitionId(), event.getNewReplicaIndex());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearPartitionReplica(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearPartitionReplica(int partitionId, int durabilityThreshold) {
        Iterator iterator = this.containers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if (this.getPartitionId((String)entry.getKey()) != partitionId || durabilityThreshold != -1 && durabilityThreshold <= ((CardinalityEstimatorContainer)entry.getValue()).getTotalBackupCount()) continue;
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
        CardinalityEstimatorContainerCollector collector = new CardinalityEstimatorContainerCollector(this.nodeEngine, this.containers);
        collector.run();
        return new Merger(collector);
    }

    private class Merger
    extends AbstractContainerMerger<CardinalityEstimatorContainer, HyperLogLog, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes> {
        Merger(CardinalityEstimatorContainerCollector collector) {
            super(collector, CardinalityEstimatorService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "cardinality estimator";
        }

        @Override
        public void runInternal() {
            CardinalityEstimatorContainerCollector collector = (CardinalityEstimatorContainerCollector)this.collector;
            ConcurrentMap containerMap = collector.getCollectedContainers();
            for (Map.Entry entry : containerMap.entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containerList = (Collection)entry.getValue();
                for (CardinalityEstimatorContainer container : containerList) {
                    String containerName = collector.getContainerName(container);
                    SplitBrainMergePolicy<HyperLogLog, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes> mergePolicy = this.getMergePolicy(collector.getMergePolicyConfig(container));
                    MergeOperation operation = new MergeOperation(containerName, mergePolicy, container.hll);
                    this.invoke(CardinalityEstimatorService.SERVICE_NAME, operation, partitionId);
                }
            }
        }
    }
}

