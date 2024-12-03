/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.cluster.ClusterStateListener;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapQuorumAwareService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.LockInterceptorService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.NotifiableEventListener;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareService;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.ReplicationSupportingService;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.CountingMigrationAwareService;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.transaction.TransactionalObject;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.wan.WanReplicationEvent;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public class MapService
implements ManagedService,
FragmentedMigrationAwareService,
TransactionalService,
RemoteService,
EventPublishingService<Object, ListenerAdapter>,
PostJoinAwareService,
SplitBrainHandlerService,
ReplicationSupportingService,
StatisticsAwareService<LocalMapStats>,
PartitionAwareService,
ClientAwareService,
QuorumAwareService,
NotifiableEventListener,
ClusterStateListener,
LockInterceptorService<Data> {
    public static final String SERVICE_NAME = "hz:impl:mapService";
    protected ManagedService managedService;
    protected CountingMigrationAwareService migrationAwareService;
    protected TransactionalService transactionalService;
    protected RemoteService remoteService;
    protected EventPublishingService eventPublishingService;
    protected PostJoinAwareService postJoinAwareService;
    protected SplitBrainHandlerService splitBrainHandlerService;
    protected ReplicationSupportingService replicationSupportingService;
    protected StatisticsAwareService statisticsAwareService;
    protected PartitionAwareService partitionAwareService;
    protected ClientAwareService clientAwareService;
    protected MapQuorumAwareService quorumAwareService;
    protected MapServiceContext mapServiceContext;

    @Override
    public void dispatchEvent(Object event, ListenerAdapter listener) {
        this.eventPublishingService.dispatchEvent(event, listener);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.managedService.init(nodeEngine, properties);
    }

    @Override
    public void reset() {
        this.managedService.reset();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.managedService.shutdown(terminate);
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        return this.migrationAwareService.getAllServiceNamespaces(event);
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return this.migrationAwareService.isKnownServiceNamespace(namespace);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        return this.migrationAwareService.prepareReplicationOperation(event);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        return this.migrationAwareService.prepareReplicationOperation(event, namespaces);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
        this.migrationAwareService.beforeMigration(event);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        this.migrationAwareService.commitMigration(event);
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        this.migrationAwareService.rollbackMigration(event);
    }

    @Override
    public Operation getPostJoinOperation() {
        return this.postJoinAwareService.getPostJoinOperation();
    }

    @Override
    public DistributedObject createDistributedObject(String objectName) {
        return this.remoteService.createDistributedObject(objectName);
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        this.remoteService.destroyDistributedObject(objectName);
        this.quorumAwareService.onDestroy(objectName);
    }

    @Override
    public void onReplicationEvent(WanReplicationEvent replicationEvent) {
        this.replicationSupportingService.onReplicationEvent(replicationEvent);
    }

    @Override
    public void onPartitionLost(IPartitionLostEvent partitionLostEvent) {
        this.partitionAwareService.onPartitionLost(partitionLostEvent);
    }

    @Override
    public Runnable prepareMergeRunnable() {
        return this.splitBrainHandlerService.prepareMergeRunnable();
    }

    @Override
    public <T extends TransactionalObject> T createTransactionalObject(String name, Transaction transaction) {
        return this.transactionalService.createTransactionalObject(name, transaction);
    }

    @Override
    public void rollbackTransaction(String transactionId) {
        this.transactionalService.rollbackTransaction(transactionId);
    }

    @Override
    public Map<String, LocalMapStats> getStats() {
        return this.statisticsAwareService.getStats();
    }

    @Override
    public String getQuorumName(String name) {
        return this.quorumAwareService.getQuorumName(name);
    }

    public MapServiceContext getMapServiceContext() {
        return this.mapServiceContext;
    }

    @Override
    public void clientDisconnected(String clientUuid) {
        this.clientAwareService.clientDisconnected(clientUuid);
    }

    public void onRegister(Object service, String serviceName, String topic, EventRegistration registration) {
        EventFilter filter = registration.getFilter();
        if (!(filter instanceof EventListenerFilter) || !filter.eval(EntryEventType.INVALIDATION.getType())) {
            return;
        }
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(topic);
        mapContainer.increaseInvalidationListenerCount();
    }

    public void onDeregister(Object service, String serviceName, String topic, EventRegistration registration) {
        EventFilter filter = registration.getFilter();
        if (!(filter instanceof EventListenerFilter) || !filter.eval(EntryEventType.INVALIDATION.getType())) {
            return;
        }
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(topic);
        mapContainer.decreaseInvalidationListenerCount();
    }

    public int getMigrationStamp() {
        return this.migrationAwareService.getMigrationStamp();
    }

    public boolean validateMigrationStamp(int stamp) {
        return this.migrationAwareService.validateMigrationStamp(stamp);
    }

    @Override
    public void onClusterStateChange(ClusterState newState) {
        this.mapServiceContext.onClusterStateChange(newState);
    }

    @Override
    public void onBeforeLock(String distributedObjectName, Data key) {
        int partitionId = this.mapServiceContext.getNodeEngine().getPartitionService().getPartitionId(key);
        RecordStore recordStore = this.mapServiceContext.getRecordStore(partitionId, distributedObjectName);
        recordStore.getRecordOrNull(key);
    }

    public static ObjectNamespace getObjectNamespace(String mapName) {
        return new DistributedObjectNamespace(SERVICE_NAME, mapName);
    }
}

