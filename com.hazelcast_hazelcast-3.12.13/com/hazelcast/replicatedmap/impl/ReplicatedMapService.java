/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.monitor.impl.LocalReplicatedMapStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.replicatedmap.ReplicatedMapCantBeCreatedOnLiteMemberException;
import com.hazelcast.replicatedmap.impl.PartitionContainer;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapProxy;
import com.hazelcast.replicatedmap.impl.ReplicatedMapSplitBrainHandlerService;
import com.hazelcast.replicatedmap.impl.operation.CheckReplicaVersionOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicationOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.replicatedmap.merge.MergePolicyProvider;
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
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.ExceptionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ReplicatedMapService
implements ManagedService,
RemoteService,
EventPublishingService<Object, Object>,
MigrationAwareService,
SplitBrainHandlerService,
StatisticsAwareService<LocalReplicatedMapStats>,
QuorumAwareService {
    public static final String SERVICE_NAME = "hz:impl:replicatedMapService";
    public static final int INVOCATION_TRY_COUNT = 3;
    private static final int SYNC_INTERVAL_SECONDS = 30;
    private static final Object NULL_OBJECT = new Object();
    private final AntiEntropyTask antiEntropyTask = new AntiEntropyTask();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            ReplicatedMapConfig lockConfig = ReplicatedMapService.this.nodeEngine.getConfig().findReplicatedMapConfig(name);
            String quorumName = lockConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private final ConcurrentHashMap<String, LocalReplicatedMapStatsImpl> statsMap = new ConcurrentHashMap();
    private final ConstructorFunction<String, LocalReplicatedMapStatsImpl> statsConstructorFunction = new ConstructorFunction<String, LocalReplicatedMapStatsImpl>(){

        @Override
        public LocalReplicatedMapStatsImpl createNew(String arg) {
            return new LocalReplicatedMapStatsImpl();
        }
    };
    private final Config config;
    private final NodeEngine nodeEngine;
    private final PartitionContainer[] partitionContainers;
    private final InternalPartitionServiceImpl partitionService;
    private final ClusterService clusterService;
    private final OperationService operationService;
    private final QuorumService quorumService;
    private final ReplicatedMapEventPublishingService eventPublishingService;
    private final ReplicatedMapSplitBrainHandlerService splitBrainHandlerService;
    private ScheduledFuture antiEntropyFuture;
    private MergePolicyProvider mergePolicyProvider;

    public ReplicatedMapService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.config = nodeEngine.getConfig();
        this.partitionService = (InternalPartitionServiceImpl)nodeEngine.getPartitionService();
        this.clusterService = nodeEngine.getClusterService();
        this.operationService = nodeEngine.getOperationService();
        this.partitionContainers = new PartitionContainer[nodeEngine.getPartitionService().getPartitionCount()];
        this.eventPublishingService = new ReplicatedMapEventPublishingService(this);
        this.splitBrainHandlerService = new ReplicatedMapSplitBrainHandlerService(this);
        this.quorumService = nodeEngine.getQuorumService();
        this.mergePolicyProvider = new MergePolicyProvider(nodeEngine);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        for (int i = 0; i < nodeEngine.getPartitionService().getPartitionCount(); ++i) {
            this.partitionContainers[i] = new PartitionContainer(this, i);
        }
        this.antiEntropyFuture = nodeEngine.getExecutionService().getGlobalTaskScheduler().scheduleWithRepetition(this.antiEntropyTask, 0L, 30L, TimeUnit.SECONDS);
    }

    @Override
    public void reset() {
        for (int i = 0; i < this.nodeEngine.getPartitionService().getPartitionCount(); ++i) {
            ConcurrentMap<String, ReplicatedRecordStore> stores = this.partitionContainers[i].getStores();
            for (ReplicatedRecordStore store : stores.values()) {
                store.reset();
            }
        }
    }

    @Override
    public void shutdown(boolean terminate) {
        for (PartitionContainer container : this.partitionContainers) {
            if (container == null) continue;
            container.shutdown();
        }
        if (this.antiEntropyFuture != null) {
            this.antiEntropyFuture.cancel(true);
        }
    }

    public LocalReplicatedMapStatsImpl getLocalMapStatsImpl(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statsMap, name, this.statsConstructorFunction);
    }

    public LocalReplicatedMapStatsImpl createReplicatedMapStats(String name) {
        LocalReplicatedMapStatsImpl stats = this.getLocalMapStatsImpl(name);
        long hits = 0L;
        long count = 0L;
        long memoryUsage = 0L;
        boolean isBinary = this.getReplicatedMapConfig(name).getInMemoryFormat() == InMemoryFormat.BINARY;
        for (PartitionContainer container : this.partitionContainers) {
            ReplicatedRecordStore store = container.getRecordStore(name);
            if (store == null) continue;
            Iterator<ReplicatedRecord> iterator = store.recordIterator();
            while (iterator.hasNext()) {
                ReplicatedRecord record = iterator.next();
                stats.setLastAccessTime(Math.max(stats.getLastAccessTime(), record.getLastAccessTime()));
                stats.setLastUpdateTime(Math.max(stats.getLastUpdateTime(), record.getUpdateTime()));
                hits += record.getHits();
                if (isBinary) {
                    memoryUsage += (long)((HeapData)record.getValueInternal()).getHeapCost();
                }
                ++count;
            }
        }
        stats.setOwnedEntryCount(count);
        stats.setHits(hits);
        stats.setOwnedEntryMemoryCost(memoryUsage);
        return stats;
    }

    @Override
    public DistributedObject createDistributedObject(String objectName) {
        ReplicatedMapConfig replicatedMapConfig = this.getReplicatedMapConfig(objectName);
        ConfigValidator.checkReplicatedMapConfig(replicatedMapConfig, this.mergePolicyProvider);
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            throw new ReplicatedMapCantBeCreatedOnLiteMemberException(this.nodeEngine.getThisAddress());
        }
        for (int i = 0; i < this.nodeEngine.getPartitionService().getPartitionCount(); ++i) {
            PartitionContainer partitionContainer = this.partitionContainers[i];
            if (partitionContainer == null) continue;
            partitionContainer.getOrCreateRecordStore(objectName);
        }
        return new ReplicatedMapProxy(this.nodeEngine, objectName, this, replicatedMapConfig);
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            return;
        }
        for (int i = 0; i < this.nodeEngine.getPartitionService().getPartitionCount(); ++i) {
            this.partitionContainers[i].destroy(objectName);
        }
        this.quorumConfigCache.remove(objectName);
    }

    @Override
    public void dispatchEvent(Object event, Object listener) {
        this.eventPublishingService.dispatchEvent(event, listener);
    }

    public ReplicatedMapConfig getReplicatedMapConfig(String name) {
        return this.config.findReplicatedMapConfig(name);
    }

    public ReplicatedRecordStore getReplicatedRecordStore(String name, boolean create, Object key) {
        return this.getReplicatedRecordStore(name, create, this.partitionService.getPartitionId(key));
    }

    public ReplicatedRecordStore getReplicatedRecordStore(String name, boolean create, int partitionId) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            throw new ReplicatedMapCantBeCreatedOnLiteMemberException(this.nodeEngine.getThisAddress());
        }
        PartitionContainer partitionContainer = this.partitionContainers[partitionId];
        if (create) {
            return partitionContainer.getOrCreateRecordStore(name);
        }
        return partitionContainer.getRecordStore(name);
    }

    public Collection<ReplicatedRecordStore> getAllReplicatedRecordStores(String name) {
        int partitionCount = this.nodeEngine.getPartitionService().getPartitionCount();
        ArrayList<ReplicatedRecordStore> stores = new ArrayList<ReplicatedRecordStore>(partitionCount);
        for (int i = 0; i < partitionCount; ++i) {
            ReplicatedRecordStore recordStore;
            PartitionContainer partitionContainer = this.partitionContainers[i];
            if (partitionContainer == null || (recordStore = partitionContainer.getRecordStore(name)) == null) continue;
            stores.add(recordStore);
        }
        return stores;
    }

    private Collection<Address> getMemberAddresses(MemberSelector memberSelector) {
        Collection<Member> members = this.clusterService.getMembers(memberSelector);
        ArrayList<Address> addresses = new ArrayList<Address>(members.size());
        for (Member member : members) {
            addresses.add(member.getAddress());
        }
        return addresses;
    }

    public void initializeListeners(String name) {
        List<ListenerConfig> listenerConfigs = this.getReplicatedMapConfig(name).getListenerConfigs();
        for (ListenerConfig listenerConfig : listenerConfigs) {
            EntryListener listener = null;
            if (listenerConfig.getImplementation() != null) {
                listener = (EntryListener)listenerConfig.getImplementation();
            } else if (listenerConfig.getClassName() != null) {
                try {
                    listener = (EntryListener)ClassLoaderUtil.newInstance(this.nodeEngine.getConfigClassLoader(), listenerConfig.getClassName());
                }
                catch (Exception e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
            if (listener == null) continue;
            if (listener instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)listener)).setHazelcastInstance(this.nodeEngine.getHazelcastInstance());
            }
            this.eventPublishingService.addEventListener(listener, TrueEventFilter.INSTANCE, name);
        }
    }

    public PartitionContainer getPartitionContainer(int partitionId) {
        return this.partitionContainers[partitionId];
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public ReplicatedMapEventPublishingService getEventPublishingService() {
        return this.eventPublishingService;
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            return null;
        }
        if (event.getReplicaIndex() > 0) {
            return null;
        }
        PartitionContainer container = this.partitionContainers[event.getPartitionId()];
        SerializationService serializationService = this.nodeEngine.getSerializationService();
        ReplicationOperation operation = new ReplicationOperation(serializationService, container, event.getPartitionId());
        operation.setService(this);
        return operation.isEmpty() ? null : operation;
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
    }

    @Override
    public Runnable prepareMergeRunnable() {
        return this.splitBrainHandlerService.prepareMergeRunnable();
    }

    @Override
    public Map<String, LocalReplicatedMapStats> getStats() {
        Collection<String> maps = this.getNodeEngine().getProxyService().getDistributedObjectNames(SERVICE_NAME);
        HashMap<String, LocalReplicatedMapStats> mapStats = new HashMap<String, LocalReplicatedMapStats>(maps.size());
        for (String map : maps) {
            mapStats.put(map, this.createReplicatedMapStats(map));
        }
        return mapStats;
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    public void ensureQuorumPresent(String distributedObjectName, QuorumType requiredQuorumPermissionType) {
        this.quorumService.ensureQuorumPresent(this.getQuorumName(distributedObjectName), requiredQuorumPermissionType);
    }

    public void triggerAntiEntropy() {
        this.antiEntropyTask.triggerAntiEntropy();
    }

    public Object getMergePolicy(String name) {
        MergePolicyConfig mergePolicyConfig = this.getReplicatedMapConfig(name).getMergePolicyConfig();
        return this.mergePolicyProvider.getMergePolicy(mergePolicyConfig.getPolicy());
    }

    private class AntiEntropyTask
    implements Runnable {
        private AntiEntropyTask() {
        }

        @Override
        public void run() {
            this.triggerAntiEntropy();
        }

        void triggerAntiEntropy() {
            if (ReplicatedMapService.this.nodeEngine.getLocalMember().isLiteMember() || ReplicatedMapService.this.clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR) == 1) {
                return;
            }
            ArrayList addresses = new ArrayList(ReplicatedMapService.this.getMemberAddresses(MemberSelectors.DATA_MEMBER_SELECTOR));
            addresses.remove(ReplicatedMapService.this.nodeEngine.getThisAddress());
            for (int i = 0; i < ReplicatedMapService.this.partitionContainers.length; ++i) {
                PartitionContainer partitionContainer;
                InternalPartition partition;
                Address ownerAddress;
                Address thisAddress = ReplicatedMapService.this.nodeEngine.getThisAddress();
                if (!thisAddress.equals(ownerAddress = (partition = ReplicatedMapService.this.partitionService.getPartition(i, false)).getOwnerOrNull()) || (partitionContainer = ReplicatedMapService.this.partitionContainers[i]).isEmpty()) continue;
                for (Address address : addresses) {
                    Operation operation = new CheckReplicaVersionOperation(partitionContainer).setPartitionId(i).setValidateTarget(false);
                    ReplicatedMapService.this.operationService.createInvocationBuilder(ReplicatedMapService.SERVICE_NAME, operation, address).setTryCount(3).invoke();
                }
            }
        }
    }
}

