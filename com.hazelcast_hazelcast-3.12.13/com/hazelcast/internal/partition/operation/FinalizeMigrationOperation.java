/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.partition.MigrationEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public final class FinalizeMigrationOperation
extends AbstractPartitionOperation
implements PartitionAwareOperation,
MigrationCycleOperation {
    private final MigrationInfo migrationInfo;
    private final MigrationEndpoint endpoint;
    private final boolean success;

    public FinalizeMigrationOperation() {
        this.migrationInfo = null;
        this.endpoint = null;
        this.success = false;
    }

    public FinalizeMigrationOperation(MigrationInfo migrationInfo, MigrationEndpoint endpoint, boolean success) {
        this.migrationInfo = migrationInfo;
        this.endpoint = endpoint;
        this.success = success;
    }

    @Override
    public void run() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        int partitionId = this.migrationInfo.getPartitionId();
        if (!partitionService.getMigrationManager().removeFinalizingMigration(this.migrationInfo)) {
            throw new IllegalStateException("This migration is not registered as finalizing: " + this.migrationInfo);
        }
        if (this.isOldBackupReplicaOwner() && partitionStateManager.isMigrating(partitionId)) {
            throw new IllegalStateException("Another replica migration is started on the same partition before finalizing " + this.migrationInfo);
        }
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        this.notifyServices(nodeEngine);
        if (this.endpoint == MigrationEndpoint.SOURCE && this.success) {
            this.commitSource();
        } else if (this.endpoint == MigrationEndpoint.DESTINATION && !this.success) {
            this.rollbackDestination();
        }
        partitionStateManager.clearMigratingFlag(partitionId);
        if (this.success) {
            nodeEngine.onPartitionMigrate(this.migrationInfo);
        }
    }

    private void notifyServices(NodeEngineImpl nodeEngine) {
        PartitionMigrationEvent event = this.getPartitionMigrationEvent();
        Collection<MigrationAwareService> migrationAwareServices = this.getMigrationAwareServices();
        if (this.isOldBackupReplicaOwner()) {
            for (MigrationAwareService service : migrationAwareServices) {
                this.beforeMigration(event, service);
            }
        }
        for (MigrationAwareService service : migrationAwareServices) {
            this.finishMigration(event, service);
        }
    }

    private PartitionMigrationEvent getPartitionMigrationEvent() {
        int partitionId = this.getPartitionId();
        return new PartitionMigrationEvent(this.endpoint, partitionId, this.endpoint == MigrationEndpoint.SOURCE ? this.migrationInfo.getSourceCurrentReplicaIndex() : this.migrationInfo.getDestinationCurrentReplicaIndex(), this.endpoint == MigrationEndpoint.SOURCE ? this.migrationInfo.getSourceNewReplicaIndex() : this.migrationInfo.getDestinationNewReplicaIndex());
    }

    private void commitSource() {
        int partitionId = this.getPartitionId();
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
        ILogger logger = this.getLogger();
        int sourceNewReplicaIndex = this.migrationInfo.getSourceNewReplicaIndex();
        if (sourceNewReplicaIndex < 0) {
            this.clearPartitionReplicaVersions(partitionId);
            if (logger.isFinestEnabled()) {
                logger.finest("Replica versions are cleared in source after migration. partitionId=" + partitionId);
            }
        } else if (this.migrationInfo.getSourceCurrentReplicaIndex() != sourceNewReplicaIndex && sourceNewReplicaIndex > 1) {
            for (ServiceNamespace namespace : replicaManager.getNamespaces(partitionId)) {
                long[] versions = this.updatePartitionReplicaVersions(replicaManager, partitionId, namespace, sourceNewReplicaIndex - 1);
                if (!logger.isFinestEnabled()) continue;
                logger.finest("Replica versions are set after SHIFT DOWN migration. partitionId=" + partitionId + " namespace: " + namespace + " replica versions=" + Arrays.toString(versions));
            }
        }
    }

    private void clearPartitionReplicaVersions(int partitionId) {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
        for (ServiceNamespace namespace : replicaManager.getNamespaces(partitionId)) {
            replicaManager.clearPartitionReplicaVersions(partitionId, namespace);
        }
    }

    private void rollbackDestination() {
        int partitionId = this.getPartitionId();
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
        ILogger logger = this.getLogger();
        int destinationCurrentReplicaIndex = this.migrationInfo.getDestinationCurrentReplicaIndex();
        if (destinationCurrentReplicaIndex == -1) {
            this.clearPartitionReplicaVersions(partitionId);
            if (logger.isFinestEnabled()) {
                logger.finest("Replica versions are cleared in destination after failed migration. partitionId=" + partitionId);
            }
        } else {
            int replicaOffset = Math.max(this.migrationInfo.getDestinationCurrentReplicaIndex(), 1);
            for (ServiceNamespace namespace : replicaManager.getNamespaces(partitionId)) {
                long[] versions = this.updatePartitionReplicaVersions(replicaManager, partitionId, namespace, replicaOffset - 1);
                if (!logger.isFinestEnabled()) continue;
                logger.finest("Replica versions are rolled back in destination after failed migration. partitionId=" + partitionId + " namespace: " + namespace + " replica versions=" + Arrays.toString(versions));
            }
        }
    }

    private long[] updatePartitionReplicaVersions(PartitionReplicaManager replicaManager, int partitionId, ServiceNamespace namespace, int replicaIndex) {
        long[] versions = replicaManager.getPartitionReplicaVersions(partitionId, namespace);
        Arrays.fill(versions, 0, replicaIndex, 0L);
        return versions;
    }

    private void beforeMigration(PartitionMigrationEvent event, MigrationAwareService service) {
        try {
            service.beforeMigration(event);
        }
        catch (Throwable e) {
            this.getLogger().warning("Error before migration -> " + event, e);
        }
    }

    private void finishMigration(PartitionMigrationEvent event, MigrationAwareService service) {
        try {
            if (this.success) {
                service.commitMigration(event);
            } else {
                service.rollbackMigration(event);
            }
        }
        catch (Throwable e) {
            this.getLogger().warning("Error while finalizing migration -> " + event, e);
        }
    }

    private boolean isOldBackupReplicaOwner() {
        PartitionReplica source = this.migrationInfo.getSource();
        return source != null && this.migrationInfo.getSourceCurrentReplicaIndex() > 0 && source.isIdentical(this.getNodeEngine().getLocalMember());
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

