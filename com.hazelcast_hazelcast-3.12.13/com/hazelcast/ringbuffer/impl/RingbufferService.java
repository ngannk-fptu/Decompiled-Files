/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferContainerCollector;
import com.hazelcast.ringbuffer.impl.RingbufferProxy;
import com.hazelcast.ringbuffer.impl.operations.MergeOperation;
import com.hazelcast.ringbuffer.impl.operations.ReplicationOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.FragmentedMigrationAwareService;
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
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.merge.AbstractContainerMerger;
import com.hazelcast.spi.merge.RingbufferMergeData;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RingbufferService
implements ManagedService,
RemoteService,
FragmentedMigrationAwareService,
QuorumAwareService,
SplitBrainHandlerService {
    public static final String TOPIC_RB_PREFIX = "_hz_rb_";
    public static final String SERVICE_NAME = "hz:impl:ringbufferService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<Integer, Map<ObjectNamespace, RingbufferContainer>> containers = new ConcurrentHashMap<Integer, Map<ObjectNamespace, RingbufferContainer>>();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            RingbufferConfig config = RingbufferService.this.nodeEngine.getConfig().findRingbufferConfig(name);
            String quorumName = config.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private NodeEngine nodeEngine;
    private SerializationService serializationService;
    private IPartitionService partitionService;
    private QuorumService quorumService;

    public RingbufferService(NodeEngineImpl nodeEngine) {
        this.init(nodeEngine, null);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = Preconditions.checkNotNull(nodeEngine, "nodeEngine can't be null");
        this.serializationService = nodeEngine.getSerializationService();
        this.partitionService = nodeEngine.getPartitionService();
        this.quorumService = nodeEngine.getQuorumService();
    }

    public ConcurrentMap<Integer, Map<ObjectNamespace, RingbufferContainer>> getContainers() {
        return this.containers;
    }

    @Override
    public DistributedObject createDistributedObject(String objectName) {
        RingbufferConfig ringbufferConfig = this.getRingbufferConfig(objectName);
        ConfigValidator.checkRingbufferConfig(ringbufferConfig, this.nodeEngine.getSplitBrainMergePolicyProvider());
        return new RingbufferProxy(this.nodeEngine, this, objectName, ringbufferConfig);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.destroyContainer(this.getRingbufferPartitionId(name), RingbufferService.getRingbufferNamespace(name));
        this.nodeEngine.getEventService().deregisterAllListeners(SERVICE_NAME, name);
        this.quorumConfigCache.remove(name);
    }

    public void destroyContainer(int partitionId, ObjectNamespace namespace) {
        Map partitionContainers = (Map)this.containers.get(partitionId);
        if (partitionContainers == null) {
            return;
        }
        partitionContainers.remove(namespace);
    }

    @Override
    public void reset() {
        this.containers.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    public <T, E> RingbufferContainer<T, E> getOrCreateContainer(int partitionId, ObjectNamespace namespace, RingbufferConfig config) {
        if (config == null) {
            throw new NullPointerException("Ringbuffer config should not be null when ringbuffer is being created");
        }
        Map<ObjectNamespace, RingbufferContainer> partitionContainers = this.getOrCreateRingbufferContainers(partitionId);
        RingbufferContainer ringbuffer = partitionContainers.get(namespace);
        if (ringbuffer != null) {
            return ringbuffer;
        }
        ringbuffer = new RingbufferContainer(namespace, config, this.nodeEngine, partitionId);
        ringbuffer.getStore().instrument(this.nodeEngine);
        partitionContainers.put(namespace, ringbuffer);
        return ringbuffer;
    }

    public <T, E> RingbufferContainer<T, E> getContainerOrNull(int partitionId, ObjectNamespace namespace) {
        Map partitionContainers = (Map)this.containers.get(partitionId);
        return partitionContainers != null ? (RingbufferContainer)partitionContainers.get(namespace) : null;
    }

    private Map<ObjectNamespace, RingbufferContainer> getOrCreateRingbufferContainers(int partitionId) {
        Map partitionContainer = (Map)this.containers.get(partitionId);
        if (partitionContainer == null) {
            this.containers.putIfAbsent(partitionId, new HashMap());
        }
        return (Map)this.containers.get(partitionId);
    }

    public RingbufferConfig getRingbufferConfig(String name) {
        Config config = this.nodeEngine.getConfig();
        return config.findRingbufferConfig(RingbufferService.getConfigName(name));
    }

    public static ObjectNamespace getRingbufferNamespace(String name) {
        return new DistributedObjectNamespace(SERVICE_NAME, name);
    }

    public int getRingbufferPartitionId(String ringbufferName) {
        Object partitionAwareData = this.serializationService.toData(ringbufferName, StringPartitioningStrategy.INSTANCE);
        return this.partitionService.getPartitionId((Data)partitionAwareData);
    }

    public void addRingbuffer(int partitionId, RingbufferContainer ringbuffer, RingbufferConfig config) {
        Preconditions.checkNotNull(ringbuffer, "ringbuffer can't be null");
        ringbuffer.init(config, this.nodeEngine);
        ringbuffer.getStore().instrument(this.nodeEngine);
        this.getOrCreateRingbufferContainers(partitionId).put(ringbuffer.getNamespace(), ringbuffer);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        return this.prepareReplicationOperation(event, this.getAllServiceNamespaces(event));
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        int partitionId = event.getPartitionId();
        Map partitionContainers = (Map)this.containers.get(partitionId);
        if (MapUtil.isNullOrEmpty(partitionContainers)) {
            return null;
        }
        HashMap<ObjectNamespace, RingbufferContainer> migrationData = new HashMap<ObjectNamespace, RingbufferContainer>();
        for (ServiceNamespace namespace : namespaces) {
            ObjectNamespace ns = (ObjectNamespace)namespace;
            RingbufferContainer container = (RingbufferContainer)partitionContainers.get(ns);
            if (container == null || container.getConfig().getTotalBackupCount() < event.getReplicaIndex()) continue;
            migrationData.put(ns, container);
        }
        if (migrationData.isEmpty()) {
            return null;
        }
        return new ReplicationOperation(migrationData, event.getPartitionId(), event.getReplicaIndex());
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearRingbuffersHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearRingbuffersHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearRingbuffersHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        Map partitionContainers = (Map)this.containers.get(partitionId);
        if (partitionContainers == null || partitionContainers.isEmpty()) {
            return;
        }
        Iterator iterator = partitionContainers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            RingbufferContainer container = (RingbufferContainer)entry.getValue();
            if (thresholdReplicaIndex >= 0 && container.getConfig().getTotalBackupCount() >= thresholdReplicaIndex) continue;
            iterator.remove();
        }
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        int partitionId = event.getPartitionId();
        Map partitionContainers = (Map)this.containers.get(partitionId);
        if (partitionContainers == null || partitionContainers.isEmpty()) {
            return Collections.emptyList();
        }
        HashSet<ServiceNamespace> namespaces = new HashSet<ServiceNamespace>();
        for (RingbufferContainer container : partitionContainers.values()) {
            if (container.getConfig().getTotalBackupCount() < event.getReplicaIndex()) continue;
            namespaces.add(container.getNamespace());
        }
        return namespaces;
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return namespace instanceof ObjectNamespace;
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
        RingbufferContainerCollector collector = new RingbufferContainerCollector(this.nodeEngine, this.containers);
        collector.run();
        return new Merger(collector);
    }

    private static String getConfigName(String name) {
        if (name.startsWith(TOPIC_RB_PREFIX)) {
            name = name.substring(TOPIC_RB_PREFIX.length());
        }
        return name;
    }

    private class Merger
    extends AbstractContainerMerger<RingbufferContainer, RingbufferMergeData, SplitBrainMergeTypes.RingbufferMergeTypes> {
        Merger(RingbufferContainerCollector collector) {
            super(collector, RingbufferService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "ringbuffer";
        }

        @Override
        public void runInternal() {
            for (Map.Entry entry : this.collector.getCollectedContainers().entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containerList = (Collection)entry.getValue();
                for (RingbufferContainer container : containerList) {
                    SplitBrainMergePolicy<RingbufferMergeData, SplitBrainMergeTypes.RingbufferMergeTypes> mergePolicy = this.getMergePolicy(container.getConfig().getMergePolicyConfig());
                    this.sendBatch(partitionId, mergePolicy, container);
                }
            }
        }

        private void sendBatch(int partitionId, SplitBrainMergePolicy<RingbufferMergeData, SplitBrainMergeTypes.RingbufferMergeTypes> mergePolicy, RingbufferContainer mergingContainer) {
            MergeOperation operation = new MergeOperation(mergingContainer.getNamespace(), mergePolicy, mergingContainer.getRingbuffer());
            this.invoke(RingbufferService.SERVICE_NAME, operation, partitionId);
        }
    }
}

