/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.Member;
import com.hazelcast.core.MigrationEvent;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.partition.MigrationEndpoint;
import java.io.IOException;
import java.util.Collection;

abstract class AbstractPromotionOperation
extends AbstractPartitionOperation
implements PartitionAwareOperation,
MigrationCycleOperation {
    protected final MigrationInfo migrationInfo;

    AbstractPromotionOperation(MigrationInfo migrationInfo) {
        this.migrationInfo = migrationInfo;
    }

    void sendMigrationEvent(MigrationEvent.MigrationStatus status) {
        int partitionId = this.getPartitionId();
        NodeEngine nodeEngine = this.getNodeEngine();
        Member localMember = nodeEngine.getLocalMember();
        MigrationEvent event = new MigrationEvent(partitionId, null, localMember, status);
        EventService eventService = nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:core:partitionService", ".migration");
        eventService.publishEvent("hz:core:partitionService", registrations, (Object)event, partitionId);
    }

    PartitionMigrationEvent getPartitionMigrationEvent() {
        return new PartitionMigrationEvent(MigrationEndpoint.DESTINATION, this.getPartitionId(), this.migrationInfo.getDestinationCurrentReplicaIndex(), 0);
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }
}

