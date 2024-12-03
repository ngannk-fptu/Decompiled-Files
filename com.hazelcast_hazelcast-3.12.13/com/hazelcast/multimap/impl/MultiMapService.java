/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockStoreInfo;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryListener;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.monitor.LocalMultiMapStats;
import com.hazelcast.monitor.impl.LocalMultiMapStatsImpl;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapContainerCollector;
import com.hazelcast.multimap.impl.MultiMapEventFilter;
import com.hazelcast.multimap.impl.MultiMapEventsDispatcher;
import com.hazelcast.multimap.impl.MultiMapEventsPublisher;
import com.hazelcast.multimap.impl.MultiMapMergeContainer;
import com.hazelcast.multimap.impl.MultiMapPartitionContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.ObjectMultiMapProxy;
import com.hazelcast.multimap.impl.operations.MergeOperation;
import com.hazelcast.multimap.impl.operations.MultiMapReplicationOperation;
import com.hazelcast.multimap.impl.txn.TransactionalMultiMapProxy;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.LockInterceptorService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.merge.AbstractContainerMerger;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.transaction.TransactionalObject;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MultiMapService
implements ManagedService,
RemoteService,
FragmentedMigrationAwareService,
EventPublishingService<EventData, EntryListener>,
TransactionalService,
StatisticsAwareService<LocalMultiMapStats>,
QuorumAwareService,
SplitBrainHandlerService,
LockInterceptorService<Data> {
    public static final String SERVICE_NAME = "hz:impl:multiMapService";
    private static final Object NULL_OBJECT = new Object();
    private static final int STATS_MAP_INITIAL_CAPACITY = 1000;
    private static final int REPLICA_ADDRESS_TRY_COUNT = 3;
    private static final int REPLICA_ADDRESS_SLEEP_WAIT_MILLIS = 1000;
    private final NodeEngine nodeEngine;
    private final MultiMapPartitionContainer[] partitionContainers;
    private final ConcurrentMap<String, LocalMultiMapStatsImpl> statsMap = MapUtil.createConcurrentHashMap(1000);
    private final ConstructorFunction<String, LocalMultiMapStatsImpl> localMultiMapStatsConstructorFunction = new ConstructorFunction<String, LocalMultiMapStatsImpl>(){

        @Override
        public LocalMultiMapStatsImpl createNew(String key) {
            return new LocalMultiMapStatsImpl();
        }
    };
    private final MultiMapEventsDispatcher dispatcher;
    private final MultiMapEventsPublisher publisher;
    private final QuorumService quorumService;
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            MultiMapConfig multiMapConfig = MultiMapService.this.nodeEngine.getConfig().findMultiMapConfig(name);
            String quorumName = multiMapConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };

    public MultiMapService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.partitionContainers = new MultiMapPartitionContainer[partitionCount];
        this.dispatcher = new MultiMapEventsDispatcher(this, nodeEngine.getClusterService());
        this.publisher = new MultiMapEventsPublisher(nodeEngine);
        this.quorumService = nodeEngine.getQuorumService();
    }

    @Override
    public void init(final NodeEngine nodeEngine, Properties properties) {
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        for (int partition = 0; partition < partitionCount; ++partition) {
            this.partitionContainers[partition] = new MultiMapPartitionContainer(this, partition);
        }
        LockService lockService = (LockService)nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService != null) {
            lockService.registerLockStoreConstructor(SERVICE_NAME, new ConstructorFunction<ObjectNamespace, LockStoreInfo>(){

                @Override
                public LockStoreInfo createNew(ObjectNamespace key) {
                    String name = key.getObjectName();
                    final MultiMapConfig multiMapConfig = nodeEngine.getConfig().findMultiMapConfig(name);
                    return new LockStoreInfo(){

                        @Override
                        public int getBackupCount() {
                            return multiMapConfig.getBackupCount();
                        }

                        @Override
                        public int getAsyncBackupCount() {
                            return multiMapConfig.getAsyncBackupCount();
                        }
                    };
                }
            });
        }
    }

    @Override
    public void reset() {
        for (MultiMapPartitionContainer container : this.partitionContainers) {
            if (container == null) continue;
            container.destroy();
        }
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
        for (int i = 0; i < this.partitionContainers.length; ++i) {
            this.partitionContainers[i] = null;
        }
    }

    public MultiMapContainer getOrCreateCollectionContainer(int partitionId, String name) {
        return this.partitionContainers[partitionId].getOrCreateMultiMapContainer(name);
    }

    public MultiMapContainer getOrCreateCollectionContainerWithoutAccess(int partitionId, String name) {
        return this.partitionContainers[partitionId].getOrCreateMultiMapContainer(name, false);
    }

    public MultiMapPartitionContainer getPartitionContainer(int partitionId) {
        return this.partitionContainers[partitionId];
    }

    @Override
    public DistributedObject createDistributedObject(String name) {
        MultiMapConfig multiMapConfig = this.nodeEngine.getConfig().findMultiMapConfig(name);
        ConfigValidator.checkMultiMapConfig(multiMapConfig, this.nodeEngine.getSplitBrainMergePolicyProvider());
        return new ObjectMultiMapProxy(multiMapConfig, this, this.nodeEngine, name);
    }

    @Override
    public void destroyDistributedObject(String name) {
        for (MultiMapPartitionContainer container : this.partitionContainers) {
            if (container == null) continue;
            container.destroyMultiMap(name);
        }
        this.nodeEngine.getEventService().deregisterAllListeners(SERVICE_NAME, name);
        this.quorumConfigCache.remove(name);
    }

    public Set<Data> localKeySet(String name) {
        HashSet<Data> keySet = new HashSet<Data>();
        for (int i = 0; i < this.nodeEngine.getPartitionService().getPartitionCount(); ++i) {
            IPartition partition = this.nodeEngine.getPartitionService().getPartition(i);
            boolean isLocalPartition = partition.isLocal();
            MultiMapPartitionContainer partitionContainer = this.getPartitionContainer(i);
            MultiMapContainer multiMapContainer = isLocalPartition ? partitionContainer.getMultiMapContainer(name) : partitionContainer.getMultiMapContainerWithoutAccess(name);
            if (multiMapContainer == null || !isLocalPartition) continue;
            keySet.addAll(multiMapContainer.keySet());
        }
        this.getLocalMultiMapStatsImpl(name).incrementOtherOperations();
        return keySet;
    }

    public SerializationService getSerializationService() {
        return this.nodeEngine.getSerializationService();
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public void publishMultiMapEvent(String mapName, EntryEventType eventType, int numberOfEntriesAffected) {
        this.publisher.publishMultiMapEvent(mapName, eventType, numberOfEntriesAffected);
    }

    public final void publishEntryEvent(String multiMapName, EntryEventType eventType, Data key, Object newValue, Object oldValue) {
        this.publisher.publishEntryEvent(multiMapName, eventType, key, newValue, oldValue);
    }

    public String addListener(String name, EventListener listener, Data key, boolean includeValue, boolean local) {
        EventService eventService = this.nodeEngine.getEventService();
        MultiMapEventFilter filter = new MultiMapEventFilter(includeValue, key);
        EventRegistration registration = local ? eventService.registerLocalListener(SERVICE_NAME, name, filter, listener) : eventService.registerListener(SERVICE_NAME, name, filter, listener);
        return registration.getId();
    }

    public boolean removeListener(String name, String registrationId) {
        EventService eventService = this.nodeEngine.getEventService();
        return eventService.deregisterListener(SERVICE_NAME, name, registrationId);
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        MultiMapPartitionContainer partitionContainer = this.partitionContainers[event.getPartitionId()];
        if (partitionContainer == null) {
            return null;
        }
        return partitionContainer.getAllNamespaces(event.getReplicaIndex());
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return namespace instanceof ObjectNamespace && SERVICE_NAME.equals(namespace.getServiceName());
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        MultiMapPartitionContainer partitionContainer = this.partitionContainers[event.getPartitionId()];
        if (partitionContainer == null) {
            return null;
        }
        return this.prepareReplicationOperation(event, partitionContainer.getAllNamespaces(event.getReplicaIndex()));
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        MultiMapPartitionContainer partitionContainer = this.partitionContainers[event.getPartitionId()];
        if (partitionContainer == null) {
            return null;
        }
        int replicaIndex = event.getReplicaIndex();
        Map<String, Map<Data, MultiMapValue>> map = MapUtil.createHashMap(namespaces.size());
        for (ServiceNamespace namespace : namespaces) {
            assert (this.isKnownServiceNamespace(namespace)) : namespace + " is not a MultiMapService namespace!";
            ObjectNamespace ns = (ObjectNamespace)namespace;
            MultiMapContainer container = (MultiMapContainer)partitionContainer.containerMap.get(ns.getObjectName());
            if (container == null || container.getConfig().getTotalBackupCount() < replicaIndex) continue;
            map.put(ns.getObjectName(), container.getMultiMapValues());
        }
        return map.isEmpty() ? null : new MultiMapReplicationOperation(map);
    }

    public void insertMigratedData(int partitionId, Map<String, Map<Data, MultiMapValue>> map) {
        for (Map.Entry<String, Map<Data, MultiMapValue>> entry : map.entrySet()) {
            String name = entry.getKey();
            MultiMapContainer container = this.getOrCreateCollectionContainerWithoutAccess(partitionId, name);
            Map<Data, MultiMapValue> collections = entry.getValue();
            long maxRecordId = -1L;
            for (Map.Entry<Data, MultiMapValue> multiMapValueEntry : collections.entrySet()) {
                MultiMapValue multiMapValue = multiMapValueEntry.getValue();
                container.getMultiMapValues().put(multiMapValueEntry.getKey(), multiMapValue);
                long recordId = this.getMaxRecordId(multiMapValue);
                maxRecordId = Math.max(maxRecordId, recordId);
            }
            container.setId(maxRecordId);
        }
    }

    private long getMaxRecordId(MultiMapValue multiMapValue) {
        long maxRecordId = -1L;
        for (MultiMapRecord record : multiMapValue.getCollection(false)) {
            maxRecordId = Math.max(maxRecordId, record.getRecordId());
        }
        return maxRecordId;
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearMapsHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearMapsHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearMapsHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        MultiMapPartitionContainer partitionContainer = this.partitionContainers[partitionId];
        if (partitionContainer == null) {
            return;
        }
        ConcurrentMap<String, MultiMapContainer> containerMap = partitionContainer.containerMap;
        if (thresholdReplicaIndex < 0) {
            for (MultiMapContainer container : containerMap.values()) {
                container.destroy();
            }
            containerMap.clear();
            return;
        }
        Iterator iterator = containerMap.values().iterator();
        while (iterator.hasNext()) {
            MultiMapContainer container = (MultiMapContainer)iterator.next();
            if (thresholdReplicaIndex <= container.getConfig().getTotalBackupCount()) continue;
            container.destroy();
            iterator.remove();
        }
    }

    public LocalMultiMapStats createStats(String name) {
        LocalMultiMapStatsImpl stats = this.getLocalMultiMapStatsImpl(name);
        long ownedEntryCount = 0L;
        long backupEntryCount = 0L;
        long hits = 0L;
        long lockedEntryCount = 0L;
        long lastAccessTime = 0L;
        long lastUpdateTime = 0L;
        ClusterService clusterService = this.nodeEngine.getClusterService();
        MultiMapConfig config = this.nodeEngine.getConfig().findMultiMapConfig(name);
        int backupCount = config.getTotalBackupCount();
        Address thisAddress = clusterService.getThisAddress();
        for (int partitionId = 0; partitionId < this.nodeEngine.getPartitionService().getPartitionCount(); ++partitionId) {
            Address owner;
            IPartition partition = this.nodeEngine.getPartitionService().getPartition(partitionId, false);
            MultiMapPartitionContainer partitionContainer = this.getPartitionContainer(partitionId);
            MultiMapContainer multiMapContainer = partitionContainer.getMultiMapContainerWithoutAccess(name);
            if (multiMapContainer == null || (owner = partition.getOwnerOrNull()) == null) continue;
            if (owner.equals(thisAddress)) {
                lockedEntryCount += multiMapContainer.getLockedCount();
                lastAccessTime = Math.max(lastAccessTime, multiMapContainer.getLastAccessTime());
                lastUpdateTime = Math.max(lastUpdateTime, multiMapContainer.getLastUpdateTime());
                for (MultiMapValue multiMapValue : multiMapContainer.getMultiMapValues().values()) {
                    hits += multiMapValue.getHits();
                    ownedEntryCount += (long)multiMapValue.getCollection(false).size();
                }
                continue;
            }
            for (int j = 1; j <= backupCount; ++j) {
                Address replicaAddress = this.getReplicaAddress(partition, backupCount, j);
                if (replicaAddress == null || !replicaAddress.equals(thisAddress)) continue;
                for (MultiMapValue multiMapValue : multiMapContainer.getMultiMapValues().values()) {
                    backupEntryCount += (long)multiMapValue.getCollection(false).size();
                }
            }
        }
        stats.setOwnedEntryCount(ownedEntryCount);
        stats.setBackupEntryCount(backupEntryCount);
        stats.setHits(hits);
        stats.setLockedEntryCount(lockedEntryCount);
        stats.setBackupCount(backupCount);
        stats.setLastAccessTime(lastAccessTime);
        stats.setLastUpdateTime(lastUpdateTime);
        return stats;
    }

    public LocalMultiMapStatsImpl getLocalMultiMapStatsImpl(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statsMap, name, this.localMultiMapStatsConstructorFunction);
    }

    @Override
    public <T extends TransactionalObject> T createTransactionalObject(String name, Transaction transaction) {
        return (T)new TransactionalMultiMapProxy(this.nodeEngine, this, name, transaction);
    }

    @Override
    public void rollbackTransaction(String transactionId) {
    }

    @Override
    public void dispatchEvent(EventData event, EntryListener listener) {
        this.dispatcher.dispatchEvent(event, listener);
    }

    @Override
    public Map<String, LocalMultiMapStats> getStats() {
        HashMap<String, LocalMultiMapStats> multiMapStats = new HashMap<String, LocalMultiMapStats>();
        for (MultiMapPartitionContainer partitionContainer : this.partitionContainers) {
            if (partitionContainer == null) continue;
            for (String name : partitionContainer.containerMap.keySet()) {
                if (multiMapStats.containsKey(name)) continue;
                multiMapStats.put(name, this.createStats(name));
            }
        }
        return multiMapStats;
    }

    private Address getReplicaAddress(IPartition partition, int backupCount, int replicaIndex) {
        Address replicaAddress = partition.getReplicaAddress(replicaIndex);
        int tryCount = 3;
        int maxAllowedBackupCount = Math.min(backupCount, this.nodeEngine.getPartitionService().getMaxAllowedBackupCount());
        while (maxAllowedBackupCount > replicaIndex && replicaAddress == null && tryCount-- > 0) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw ExceptionUtil.rethrow(e);
            }
            replicaAddress = partition.getReplicaAddress(replicaIndex);
        }
        return replicaAddress;
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    public void ensureQuorumPresent(String distributedObjectName, QuorumType requiredQuorumPermissionType) {
        this.quorumService.ensureQuorumPresent(this.getQuorumName(distributedObjectName), requiredQuorumPermissionType);
    }

    @Override
    public Runnable prepareMergeRunnable() {
        MultiMapContainerCollector collector = new MultiMapContainerCollector(this.nodeEngine, this.partitionContainers);
        collector.run();
        return new Merger(collector);
    }

    @Override
    public void onBeforeLock(String distributedObjectName, Data key) {
        int partitionId = this.nodeEngine.getPartitionService().getPartitionId(key);
        MultiMapPartitionContainer partitionContainer = this.getPartitionContainer(partitionId);
        partitionContainer.getOrCreateMultiMapContainer(distributedObjectName);
    }

    private class Merger
    extends AbstractContainerMerger<MultiMapContainer, Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> {
        Merger(MultiMapContainerCollector collector) {
            super(collector, MultiMapService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "MultiMap";
        }

        @Override
        public void runInternal() {
            for (Map.Entry entry : this.collector.getCollectedContainers().entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containers = (Collection)entry.getValue();
                for (MultiMapContainer container : containers) {
                    String name = container.getObjectNamespace().getObjectName();
                    SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy = this.getMergePolicy(container.getConfig().getMergePolicyConfig());
                    int batchSize = container.getConfig().getMergePolicyConfig().getBatchSize();
                    ArrayList<MultiMapMergeContainer> mergeContainers = new ArrayList<MultiMapMergeContainer>(batchSize);
                    for (Map.Entry multiMapValueEntry : container.getMultiMapValues().entrySet()) {
                        Data key = (Data)multiMapValueEntry.getKey();
                        MultiMapValue multiMapValue = (MultiMapValue)multiMapValueEntry.getValue();
                        Collection<MultiMapRecord> records = multiMapValue.getCollection(false);
                        MultiMapMergeContainer mergeContainer = new MultiMapMergeContainer(key, records, container.getCreationTime(), container.getLastAccessTime(), container.getLastUpdateTime(), multiMapValue.getHits());
                        mergeContainers.add(mergeContainer);
                        if (mergeContainers.size() != batchSize) continue;
                        this.sendBatch(partitionId, name, mergePolicy, mergeContainers);
                        mergeContainers = new ArrayList(batchSize);
                    }
                    if (mergeContainers.size() <= 0) continue;
                    this.sendBatch(partitionId, name, mergePolicy, mergeContainers);
                }
            }
        }

        private void sendBatch(int partitionId, String name, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy, List<MultiMapMergeContainer> mergeContainers) {
            MergeOperation operation = new MergeOperation(name, mergeContainers, mergePolicy);
            this.invoke(MultiMapService.SERVICE_NAME, operation, partitionId);
        }
    }
}

