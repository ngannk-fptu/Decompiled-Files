/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MigrationEvent;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionEventManager;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.AbstractPromotionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.ServiceNamespace;
import java.util.Arrays;

final class FinalizePromotionOperation
extends AbstractPromotionOperation {
    private final boolean success;
    private ILogger logger;

    public FinalizePromotionOperation() {
        super(null);
        this.success = false;
    }

    FinalizePromotionOperation(MigrationInfo migrationInfo, boolean success) {
        super(migrationInfo);
        this.success = success;
    }

    @Override
    public void beforeRun() throws Exception {
        this.logger = this.getLogger();
    }

    @Override
    public void run() throws Exception {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Running finalize promotion for " + this.getPartitionMigrationEvent() + ", result: " + this.success);
        }
        if (this.success) {
            this.shiftUpReplicaVersions();
            this.commitServices();
        } else {
            this.rollbackServices();
        }
    }

    @Override
    public void afterRun() throws Exception {
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        PartitionStateManager partitionStateManager = service.getPartitionStateManager();
        partitionStateManager.clearMigratingFlag(this.getPartitionId());
        this.sendMigrationEvent(this.success ? MigrationEvent.MigrationStatus.COMPLETED : MigrationEvent.MigrationStatus.FAILED);
    }

    private void shiftUpReplicaVersions() {
        int partitionId = this.getPartitionId();
        int currentReplicaIndex = this.migrationInfo.getDestinationCurrentReplicaIndex();
        int lostReplicaIndex = currentReplicaIndex - 1;
        try {
            InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
            PartitionReplicaVersionManager partitionReplicaVersionManager = partitionService.getPartitionReplicaVersionManager();
            for (ServiceNamespace namespace : partitionReplicaVersionManager.getNamespaces(partitionId)) {
                long[] versions = partitionReplicaVersionManager.getPartitionReplicaVersions(partitionId, namespace);
                if (currentReplicaIndex > 1) {
                    long[] versionsCopy = Arrays.copyOf(versions, versions.length);
                    long version = versions[lostReplicaIndex];
                    Arrays.fill(versions, 0, lostReplicaIndex, version);
                    if (!this.logger.isFinestEnabled()) continue;
                    this.logger.finest("Partition replica is lost! partitionId=" + partitionId + " namespace: " + namespace + " lost replicaIndex=" + lostReplicaIndex + " replica versions before shift up=" + Arrays.toString(versionsCopy) + " replica versions after shift up=" + Arrays.toString(versions));
                    continue;
                }
                if (!this.logger.isFinestEnabled()) continue;
                this.logger.finest("PROMOTE partitionId=" + this.getPartitionId() + " namespace: " + namespace + " from currentReplicaIndex=" + currentReplicaIndex);
            }
            PartitionEventManager partitionEventManager = partitionService.getPartitionEventManager();
            partitionEventManager.sendPartitionLostEvent(partitionId, lostReplicaIndex);
        }
        catch (Throwable e) {
            this.logger.warning("Promotion failed. partitionId=" + partitionId + " replicaIndex=" + currentReplicaIndex, e);
        }
    }

    private void commitServices() {
        PartitionMigrationEvent event = this.getPartitionMigrationEvent();
        for (MigrationAwareService service : this.getMigrationAwareServices()) {
            try {
                service.commitMigration(event);
            }
            catch (Throwable e) {
                this.logger.warning("While promoting " + this.getPartitionMigrationEvent(), e);
            }
        }
    }

    private void rollbackServices() {
        PartitionMigrationEvent event = this.getPartitionMigrationEvent();
        for (MigrationAwareService service : this.getMigrationAwareServices()) {
            try {
                service.rollbackMigration(event);
            }
            catch (Throwable e) {
                this.logger.warning("While promoting " + this.getPartitionMigrationEvent(), e);
            }
        }
    }
}

