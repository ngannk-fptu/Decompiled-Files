/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.AbstractMapServiceFactory;
import com.hazelcast.map.impl.MapClientAwareService;
import com.hazelcast.map.impl.MapManagedService;
import com.hazelcast.map.impl.MapMigrationAwareService;
import com.hazelcast.map.impl.MapPartitionAwareService;
import com.hazelcast.map.impl.MapPostJoinAwareService;
import com.hazelcast.map.impl.MapQuorumAwareService;
import com.hazelcast.map.impl.MapRemoteService;
import com.hazelcast.map.impl.MapReplicationSupportingService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapSplitBrainHandlerService;
import com.hazelcast.map.impl.MapStatisticsAwareService;
import com.hazelcast.map.impl.MapTransactionalService;
import com.hazelcast.map.impl.event.MapEventPublishingService;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.ReplicationSupportingService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.CountingMigrationAwareService;
import com.hazelcast.util.Preconditions;

class DefaultMapServiceFactory
extends AbstractMapServiceFactory {
    private final NodeEngine nodeEngine;
    private final MapServiceContext mapServiceContext;

    public DefaultMapServiceFactory(NodeEngine nodeEngine, MapServiceContext mapServiceContext) {
        this.nodeEngine = Preconditions.checkNotNull(nodeEngine, "nodeEngine should not be null");
        this.mapServiceContext = Preconditions.checkNotNull(mapServiceContext, "mapServiceContext should not be null");
    }

    @Override
    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public MapServiceContext getMapServiceContext() {
        return this.mapServiceContext;
    }

    @Override
    ManagedService createManagedService() {
        return new MapManagedService(this.mapServiceContext);
    }

    @Override
    CountingMigrationAwareService createMigrationAwareService() {
        return new CountingMigrationAwareService(new MapMigrationAwareService(this.mapServiceContext));
    }

    @Override
    TransactionalService createTransactionalService() {
        return new MapTransactionalService(this.mapServiceContext);
    }

    @Override
    RemoteService createRemoteService() {
        return new MapRemoteService(this.mapServiceContext);
    }

    @Override
    EventPublishingService createEventPublishingService() {
        return new MapEventPublishingService(this.mapServiceContext);
    }

    @Override
    PostJoinAwareService createPostJoinAwareService() {
        return new MapPostJoinAwareService(this.mapServiceContext);
    }

    @Override
    SplitBrainHandlerService createSplitBrainHandlerService() {
        return new MapSplitBrainHandlerService(this.mapServiceContext);
    }

    @Override
    ReplicationSupportingService createReplicationSupportingService() {
        return new MapReplicationSupportingService(this.mapServiceContext);
    }

    @Override
    StatisticsAwareService createStatisticsAwareService() {
        return new MapStatisticsAwareService(this.mapServiceContext);
    }

    @Override
    MapPartitionAwareService createPartitionAwareService() {
        return new MapPartitionAwareService(this.mapServiceContext);
    }

    @Override
    MapQuorumAwareService createQuorumAwareService() {
        return new MapQuorumAwareService(this.getMapServiceContext());
    }

    @Override
    ClientAwareService createClientAwareService() {
        return new MapClientAwareService(this.getMapServiceContext());
    }
}

