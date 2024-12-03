/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionContainerCollector;
import com.hazelcast.collection.impl.collection.CollectionEvent;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionMergeOperation;
import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTransactionRollbackOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.core.ItemListener;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.merge.AbstractContainerMerger;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public abstract class CollectionService
implements ManagedService,
RemoteService,
EventPublishingService<CollectionEvent, ItemListener<Data>>,
TransactionalService,
MigrationAwareService,
QuorumAwareService,
SplitBrainHandlerService {
    protected final NodeEngine nodeEngine;
    protected final SerializationService serializationService;
    protected final IPartitionService partitionService;
    private final ILogger logger;

    protected CollectionService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.serializationService = nodeEngine.getSerializationService();
        this.partitionService = nodeEngine.getPartitionService();
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
        this.getContainerMap().clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    @Override
    public void destroyDistributedObject(String name) {
        CollectionContainer container = (CollectionContainer)this.getContainerMap().remove(name);
        if (container != null) {
            container.destroy();
        }
        this.nodeEngine.getEventService().deregisterAllListeners(this.getServiceName(), name);
    }

    public abstract CollectionContainer getOrCreateContainer(String var1, boolean var2);

    public abstract ConcurrentMap<String, ? extends CollectionContainer> getContainerMap();

    public abstract String getServiceName();

    @Override
    public void dispatchEvent(CollectionEvent event, ItemListener<Data> listener) {
        MemberImpl member = this.nodeEngine.getClusterService().getMember(event.getCaller());
        DataAwareItemEvent itemEvent = new DataAwareItemEvent(event.getName(), event.getEventType(), event.getData(), member, this.serializationService);
        if (member == null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Dropping event " + itemEvent + " from unknown address:" + event.getCaller());
            }
            return;
        }
        if (event.getEventType().equals((Object)ItemEventType.ADDED)) {
            listener.itemAdded(itemEvent);
        } else {
            listener.itemRemoved(itemEvent);
        }
    }

    @Override
    public void rollbackTransaction(String transactionId) {
        Set collectionNames = this.getContainerMap().keySet();
        OperationService operationService = this.nodeEngine.getOperationService();
        for (String name : collectionNames) {
            int partitionId = this.partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            Operation operation = new CollectionTransactionRollbackOperation(name, transactionId).setPartitionId(partitionId).setService(this).setNodeEngine(this.nodeEngine);
            operationService.invokeOnPartition(operation);
        }
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
    }

    protected Map<String, CollectionContainer> getMigrationData(PartitionReplicationEvent event) {
        HashMap<String, CollectionContainer> migrationData = new HashMap<String, CollectionContainer>();
        for (Map.Entry entry : this.getContainerMap().entrySet()) {
            String name = (String)entry.getKey();
            int partitionId = this.partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            CollectionContainer container = (CollectionContainer)entry.getValue();
            if (partitionId != event.getPartitionId() || container.getConfig().getTotalBackupCount() < event.getReplicaIndex()) continue;
            migrationData.put(name, container);
        }
        return migrationData;
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearCollectionsHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearCollectionsHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearCollectionsHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        Set entrySet = this.getContainerMap().entrySet();
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String name = (String)entry.getKey();
            CollectionContainer container = (CollectionContainer)entry.getValue();
            int containerPartitionId = this.partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            if (containerPartitionId != partitionId || thresholdReplicaIndex >= 0 && thresholdReplicaIndex <= container.getConfig().getTotalBackupCount()) continue;
            container.destroy();
            iterator.remove();
        }
    }

    public void addContainer(String name, CollectionContainer container) {
        this.getRawContainerMap().put(name, container);
    }

    private ConcurrentMap<String, CollectionContainer> getRawContainerMap() {
        return this.getContainerMap();
    }

    @Override
    public Runnable prepareMergeRunnable() {
        CollectionContainerCollector collector = new CollectionContainerCollector(this.nodeEngine, this.getRawContainerMap());
        collector.run();
        return new Merger(collector);
    }

    private class Merger
    extends AbstractContainerMerger<CollectionContainer, Collection<Object>, SplitBrainMergeTypes.CollectionMergeTypes> {
        Merger(CollectionContainerCollector collector) {
            super(collector, CollectionService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "collection";
        }

        @Override
        public void runInternal() {
            for (Map.Entry entry : this.collector.getCollectedContainers().entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containerList = (Collection)entry.getValue();
                for (CollectionContainer container : containerList) {
                    Collection<CollectionItem> items = container.getCollection();
                    String name = container.getName();
                    SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.CollectionMergeTypes> mergePolicy = this.getMergePolicy(container.getConfig().getMergePolicyConfig());
                    SplitBrainMergeTypes.CollectionMergeTypes mergingValue = MergingValueFactory.createMergingValue(CollectionService.this.serializationService, items);
                    this.sendBatch(partitionId, name, mergePolicy, mergingValue);
                    items.clear();
                }
            }
        }

        private void sendBatch(int partitionId, String name, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.CollectionMergeTypes> mergePolicy, SplitBrainMergeTypes.CollectionMergeTypes mergingValue) {
            CollectionMergeOperation operation = new CollectionMergeOperation(name, mergePolicy, mergingValue);
            this.invoke(CollectionService.this.getServiceName(), operation, partitionId);
        }
    }
}

