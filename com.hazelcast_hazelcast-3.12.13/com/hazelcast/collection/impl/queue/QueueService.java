/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.common.DataAwareItemEvent;
import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueContainerCollector;
import com.hazelcast.collection.impl.queue.QueueEvent;
import com.hazelcast.collection.impl.queue.QueueEventFilter;
import com.hazelcast.collection.impl.queue.QueueEvictionProcessor;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.QueueProxyImpl;
import com.hazelcast.collection.impl.queue.operations.QueueMergeOperation;
import com.hazelcast.collection.impl.queue.operations.QueueReplicationOperation;
import com.hazelcast.collection.impl.txnqueue.TransactionalQueueProxy;
import com.hazelcast.collection.impl.txnqueue.operations.QueueTransactionRollbackOperation;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.core.ItemListener;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
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
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.merge.AbstractContainerMerger;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.EntryTaskSchedulerFactory;
import com.hazelcast.util.scheduler.ScheduleType;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueueService
implements ManagedService,
MigrationAwareService,
TransactionalService,
RemoteService,
EventPublishingService<QueueEvent, ItemListener>,
StatisticsAwareService<LocalQueueStats>,
QuorumAwareService,
SplitBrainHandlerService {
    public static final String SERVICE_NAME = "hz:impl:queueService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, QueueContainer> containerMap = new ConcurrentHashMap<String, QueueContainer>();
    private final ConcurrentMap<String, LocalQueueStatsImpl> statsMap = new ConcurrentHashMap<String, LocalQueueStatsImpl>(1000);
    private final ConstructorFunction<String, LocalQueueStatsImpl> localQueueStatsConstructorFunction = new ConstructorFunction<String, LocalQueueStatsImpl>(){

        @Override
        public LocalQueueStatsImpl createNew(String key) {
            return new LocalQueueStatsImpl();
        }
    };
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            QueueConfig queueConfig = QueueService.this.nodeEngine.getConfig().findQueueConfig(name);
            String quorumName = queueConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private final NodeEngine nodeEngine;
    private final SerializationService serializationService;
    private final IPartitionService partitionService;
    private final ILogger logger;
    private final EntryTaskScheduler<String, Void> queueEvictionScheduler;

    public QueueService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.serializationService = nodeEngine.getSerializationService();
        this.partitionService = nodeEngine.getPartitionService();
        this.logger = nodeEngine.getLogger(QueueService.class);
        TaskScheduler globalScheduler = nodeEngine.getExecutionService().getGlobalTaskScheduler();
        QueueEvictionProcessor entryProcessor = new QueueEvictionProcessor(nodeEngine);
        this.queueEvictionScheduler = EntryTaskSchedulerFactory.newScheduler(globalScheduler, entryProcessor, ScheduleType.POSTPONE);
    }

    public void scheduleEviction(String name, long delay) {
        this.queueEvictionScheduler.schedule(delay, name, null);
    }

    public void cancelEviction(String name) {
        this.queueEvictionScheduler.cancel(name);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
        this.containerMap.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    public QueueContainer getOrCreateContainer(String name, boolean fromBackup) {
        QueueContainer container = (QueueContainer)this.containerMap.get(name);
        if (container != null) {
            return container;
        }
        container = new QueueContainer(name, this.nodeEngine.getConfig().findQueueConfig(name), this.nodeEngine, this);
        QueueContainer existing = this.containerMap.putIfAbsent(name, container);
        if (existing != null) {
            container = existing;
        } else {
            container.init(fromBackup);
            container.getStore().instrument(this.nodeEngine);
        }
        return container;
    }

    public void addContainer(String name, QueueContainer container) {
        this.containerMap.put(name, container);
    }

    public boolean containsQueue(String name) {
        return this.containerMap.containsKey(name);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        HashMap<String, QueueContainer> migrationData = new HashMap<String, QueueContainer>();
        for (Map.Entry entry : this.containerMap.entrySet()) {
            String name = (String)entry.getKey();
            int partitionId = this.partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            QueueContainer container = (QueueContainer)entry.getValue();
            if (partitionId != event.getPartitionId() || container.getConfig().getTotalBackupCount() < event.getReplicaIndex()) continue;
            migrationData.put(name, container);
        }
        if (migrationData.isEmpty()) {
            return null;
        }
        return new QueueReplicationOperation(migrationData, event.getPartitionId(), event.getReplicaIndex());
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearQueuesHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearQueuesHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearQueuesHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        Iterator iterator = this.containerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String name = (String)entry.getKey();
            QueueContainer container = (QueueContainer)entry.getValue();
            int containerPartitionId = this.partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            if (containerPartitionId != partitionId || thresholdReplicaIndex >= 0 && thresholdReplicaIndex <= container.getConfig().getTotalBackupCount()) continue;
            container.destroy();
            iterator.remove();
        }
    }

    @Override
    public void dispatchEvent(QueueEvent event, ItemListener listener) {
        MemberImpl member = this.nodeEngine.getClusterService().getMember(event.caller);
        DataAwareItemEvent itemEvent = new DataAwareItemEvent(event.name, event.eventType, event.data, member, this.serializationService);
        if (member == null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Dropping event " + itemEvent + " from unknown address:" + event.caller);
            }
            return;
        }
        if (event.eventType.equals((Object)ItemEventType.ADDED)) {
            listener.itemAdded(itemEvent);
        } else {
            listener.itemRemoved(itemEvent);
        }
        this.getLocalQueueStatsImpl(event.name).incrementReceivedEvents();
    }

    @Override
    public QueueProxyImpl createDistributedObject(String objectId) {
        QueueConfig queueConfig = this.nodeEngine.getConfig().findQueueConfig(objectId);
        ConfigValidator.checkQueueConfig(queueConfig, this.nodeEngine.getSplitBrainMergePolicyProvider());
        return new QueueProxyImpl(objectId, this, this.nodeEngine, queueConfig);
    }

    @Override
    public void destroyDistributedObject(String name) {
        QueueContainer container = (QueueContainer)this.containerMap.remove(name);
        if (container != null) {
            container.destroy();
        }
        this.nodeEngine.getEventService().deregisterAllListeners(SERVICE_NAME, name);
        this.quorumConfigCache.remove(name);
    }

    public String addItemListener(String name, ItemListener listener, boolean includeValue, boolean isLocal) {
        EventService eventService = this.nodeEngine.getEventService();
        QueueEventFilter filter = new QueueEventFilter(includeValue);
        EventRegistration registration = isLocal ? eventService.registerLocalListener(SERVICE_NAME, name, filter, listener) : eventService.registerListener(SERVICE_NAME, name, filter, listener);
        return registration.getId();
    }

    public boolean removeItemListener(String name, String registrationId) {
        EventService eventService = this.nodeEngine.getEventService();
        return eventService.deregisterListener(SERVICE_NAME, name, registrationId);
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public LocalQueueStats createLocalQueueStats(String name, int partitionId) {
        IPartition partition;
        Address owner;
        LocalQueueStatsImpl stats = this.getLocalQueueStatsImpl(name);
        stats.setOwnedItemCount(0);
        stats.setBackupItemCount(0);
        QueueContainer container = (QueueContainer)this.containerMap.get(name);
        if (container == null) {
            return stats;
        }
        Address thisAddress = this.nodeEngine.getClusterService().getThisAddress();
        if (thisAddress.equals(owner = (partition = this.partitionService.getPartition(partitionId, false)).getOwnerOrNull())) {
            stats.setOwnedItemCount(container.size());
        } else if (owner != null) {
            stats.setBackupItemCount(container.backupSize());
        }
        container.setStats(stats);
        return stats;
    }

    public LocalQueueStats createLocalQueueStats(String name) {
        return this.createLocalQueueStats(name, this.getPartitionId(name));
    }

    public LocalQueueStatsImpl getLocalQueueStatsImpl(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statsMap, name, this.localQueueStatsConstructorFunction);
    }

    public TransactionalQueueProxy createTransactionalObject(String name, Transaction transaction) {
        return new TransactionalQueueProxy(this.nodeEngine, this, name, transaction);
    }

    @Override
    public void rollbackTransaction(String transactionId) {
        Set queueNames = this.containerMap.keySet();
        OperationService operationService = this.nodeEngine.getOperationService();
        for (String name : queueNames) {
            int partitionId = this.partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            Operation operation = new QueueTransactionRollbackOperation(name, transactionId).setPartitionId(partitionId).setService(this).setNodeEngine(this.nodeEngine);
            operationService.invokeOnPartition(operation);
        }
    }

    @Override
    public Map<String, LocalQueueStats> getStats() {
        Map<String, LocalQueueStats> queueStats = MapUtil.createHashMap(this.containerMap.size());
        for (Map.Entry entry : this.containerMap.entrySet()) {
            String name = (String)entry.getKey();
            LocalQueueStats queueStat = this.createLocalQueueStats(name);
            queueStats.put(name, queueStat);
        }
        return queueStats;
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    @Override
    public Runnable prepareMergeRunnable() {
        QueueContainerCollector collector = new QueueContainerCollector(this.nodeEngine, this.containerMap);
        collector.run();
        return new Merger(collector);
    }

    private int getPartitionId(String name) {
        Object keyData = this.serializationService.toData(name, StringPartitioningStrategy.INSTANCE);
        return this.partitionService.getPartitionId((Data)keyData);
    }

    private class Merger
    extends AbstractContainerMerger<QueueContainer, Collection<Object>, SplitBrainMergeTypes.QueueMergeTypes> {
        Merger(QueueContainerCollector collector) {
            super(collector, QueueService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "queue";
        }

        @Override
        public void runInternal() {
            for (Map.Entry entry : this.collector.getCollectedContainers().entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containerList = (Collection)entry.getValue();
                for (QueueContainer container : containerList) {
                    Deque<QueueItem> items = container.getItemQueue();
                    String name = container.getName();
                    SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.QueueMergeTypes> mergePolicy = this.getMergePolicy(container.getConfig().getMergePolicyConfig());
                    SplitBrainMergeTypes.QueueMergeTypes mergingValue = MergingValueFactory.createMergingValue(QueueService.this.serializationService, items);
                    this.sendBatch(partitionId, name, mergePolicy, mergingValue);
                }
            }
        }

        private void sendBatch(int partitionId, String name, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.QueueMergeTypes> mergePolicy, SplitBrainMergeTypes.QueueMergeTypes mergingValue) {
            QueueMergeOperation operation = new QueueMergeOperation(name, mergePolicy, mergingValue);
            this.invoke(QueueService.SERVICE_NAME, operation, partitionId);
        }
    }
}

