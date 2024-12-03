/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.CacheRecordStore;
import com.hazelcast.cache.impl.DefaultOperationProvider;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.event.CacheWanEventPublisher;
import com.hazelcast.cache.impl.operation.CacheReplicationOperation;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.partition.MigrationEndpoint;
import java.util.Collection;

public class CacheService
extends AbstractCacheService {
    @Override
    protected CachePartitionSegment newPartitionSegment(int partitionId) {
        return new CachePartitionSegment(this, partitionId);
    }

    @Override
    protected ICacheRecordStore createNewRecordStore(String cacheNameWithPrefix, int partitionId) {
        CacheRecordStore recordStore = new CacheRecordStore(cacheNameWithPrefix, partitionId, this.nodeEngine, this);
        recordStore.instrument(this.nodeEngine);
        return recordStore;
    }

    @Override
    protected CacheOperationProvider createOperationProvider(String nameWithPrefix, InMemoryFormat inMemoryFormat) {
        return new DefaultOperationProvider(nameWithPrefix);
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        CachePartitionSegment segment = this.segments[event.getPartitionId()];
        return segment.getAllNamespaces(event.getReplicaIndex());
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return namespace instanceof ObjectNamespace && "hz:impl:cacheService".equals(namespace.getServiceName());
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        CachePartitionSegment segment = this.segments[event.getPartitionId()];
        return this.prepareReplicationOperation(event, segment.getAllNamespaces(event.getReplicaIndex()));
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        assert (this.assertAllKnownNamespaces(namespaces));
        CachePartitionSegment segment = this.segments[event.getPartitionId()];
        CacheReplicationOperation op = this.newCacheReplicationOperation();
        op.setPartitionId(event.getPartitionId());
        op.prepare(segment, namespaces, event.getReplicaIndex());
        return op.isEmpty() ? null : op;
    }

    private boolean assertAllKnownNamespaces(Collection<ServiceNamespace> namespaces) {
        for (ServiceNamespace namespace : namespaces) {
            assert (this.isKnownServiceNamespace(namespace)) : namespace + " is not a CacheService namespace!";
        }
        return true;
    }

    protected CacheReplicationOperation newCacheReplicationOperation() {
        return new CacheReplicationOperation();
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        super.commitMigration(event);
        if (MigrationEndpoint.SOURCE == event.getMigrationEndpoint()) {
            this.getMetaDataGenerator().removeUuidAndSequence(event.getPartitionId());
        } else if (MigrationEndpoint.DESTINATION == event.getMigrationEndpoint() && event.getNewReplicaIndex() != 0) {
            this.getMetaDataGenerator().regenerateUuid(event.getPartitionId());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        super.rollbackMigration(event);
        if (MigrationEndpoint.DESTINATION == event.getMigrationEndpoint()) {
            this.getMetaDataGenerator().removeUuidAndSequence(event.getPartitionId());
        }
    }

    private MetaDataGenerator getMetaDataGenerator() {
        return this.cacheEventHandler.getMetaDataGenerator();
    }

    public String toString() {
        return "CacheService[hz:impl:cacheService]";
    }

    @Override
    public boolean isWanReplicationEnabled(String cacheNameWithPrefix) {
        return false;
    }

    @Override
    public CacheWanEventPublisher getCacheWanEventPublisher() {
        throw new UnsupportedOperationException("WAN replication is not supported");
    }

    public static ObjectNamespace getObjectNamespace(String cacheName) {
        return new DistributedObjectNamespace("hz:impl:cacheService", cacheName);
    }
}

