/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapQuorumAwareService;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapServiceFactory;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareService;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.ReplicationSupportingService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.CountingMigrationAwareService;
import com.hazelcast.util.Preconditions;

abstract class AbstractMapServiceFactory
implements MapServiceFactory {
    AbstractMapServiceFactory() {
    }

    abstract ManagedService createManagedService();

    abstract CountingMigrationAwareService createMigrationAwareService();

    abstract TransactionalService createTransactionalService();

    abstract RemoteService createRemoteService();

    abstract EventPublishingService createEventPublishingService();

    abstract PostJoinAwareService createPostJoinAwareService();

    abstract SplitBrainHandlerService createSplitBrainHandlerService();

    abstract ReplicationSupportingService createReplicationSupportingService();

    abstract StatisticsAwareService createStatisticsAwareService();

    abstract PartitionAwareService createPartitionAwareService();

    abstract ClientAwareService createClientAwareService();

    abstract MapQuorumAwareService createQuorumAwareService();

    @Override
    public MapService createMapService() {
        NodeEngine nodeEngine = this.getNodeEngine();
        MapServiceContext mapServiceContext = this.getMapServiceContext();
        ManagedService managedService = this.createManagedService();
        CountingMigrationAwareService migrationAwareService = this.createMigrationAwareService();
        TransactionalService transactionalService = this.createTransactionalService();
        RemoteService remoteService = this.createRemoteService();
        EventPublishingService eventPublishingService = this.createEventPublishingService();
        PostJoinAwareService postJoinAwareService = this.createPostJoinAwareService();
        SplitBrainHandlerService splitBrainHandlerService = this.createSplitBrainHandlerService();
        ReplicationSupportingService replicationSupportingService = this.createReplicationSupportingService();
        StatisticsAwareService statisticsAwareService = this.createStatisticsAwareService();
        PartitionAwareService partitionAwareService = this.createPartitionAwareService();
        MapQuorumAwareService quorumAwareService = this.createQuorumAwareService();
        ClientAwareService clientAwareService = this.createClientAwareService();
        Preconditions.checkNotNull(nodeEngine, "nodeEngine should not be null");
        Preconditions.checkNotNull(mapServiceContext, "mapServiceContext should not be null");
        Preconditions.checkNotNull(managedService, "managedService should not be null");
        Preconditions.checkNotNull(migrationAwareService, "migrationAwareService should not be null");
        Preconditions.checkNotNull(transactionalService, "transactionalService should not be null");
        Preconditions.checkNotNull(remoteService, "remoteService should not be null");
        Preconditions.checkNotNull(eventPublishingService, "eventPublishingService should not be null");
        Preconditions.checkNotNull(postJoinAwareService, "postJoinAwareService should not be null");
        Preconditions.checkNotNull(splitBrainHandlerService, "splitBrainHandlerService should not be null");
        Preconditions.checkNotNull(replicationSupportingService, "replicationSupportingService should not be null");
        Preconditions.checkNotNull(statisticsAwareService, "statisticsAwareService should not be null");
        Preconditions.checkNotNull(partitionAwareService, "partitionAwareService should not be null");
        Preconditions.checkNotNull(quorumAwareService, "quorumAwareService should not be null");
        Preconditions.checkNotNull(clientAwareService, "clientAwareService should not be null");
        MapService mapService = new MapService();
        mapService.managedService = managedService;
        mapService.migrationAwareService = migrationAwareService;
        mapService.transactionalService = transactionalService;
        mapService.remoteService = remoteService;
        mapService.eventPublishingService = eventPublishingService;
        mapService.postJoinAwareService = postJoinAwareService;
        mapService.splitBrainHandlerService = splitBrainHandlerService;
        mapService.replicationSupportingService = replicationSupportingService;
        mapService.statisticsAwareService = statisticsAwareService;
        mapService.mapServiceContext = mapServiceContext;
        mapService.partitionAwareService = partitionAwareService;
        mapService.quorumAwareService = quorumAwareService;
        mapService.clientAwareService = clientAwareService;
        mapServiceContext.setService(mapService);
        return mapService;
    }
}

