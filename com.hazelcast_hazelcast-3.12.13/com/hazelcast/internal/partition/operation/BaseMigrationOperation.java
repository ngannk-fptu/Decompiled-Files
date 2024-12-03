/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionStateVersionMismatchException;
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.MigrationManager;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class BaseMigrationOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation,
PartitionAwareOperation,
Versioned {
    protected MigrationInfo migrationInfo;
    protected boolean success;
    protected List<MigrationInfo> completedMigrations;
    protected int partitionStateVersion;
    private transient boolean nodeStartCompleted;

    BaseMigrationOperation() {
    }

    BaseMigrationOperation(MigrationInfo migrationInfo, List<MigrationInfo> completedMigrations, int partitionStateVersion) {
        this.migrationInfo = migrationInfo;
        this.completedMigrations = completedMigrations;
        this.partitionStateVersion = partitionStateVersion;
        this.setPartitionId(migrationInfo.getPartitionId());
    }

    @Override
    public final void beforeRun() throws Exception {
        try {
            this.onMigrationStart();
            this.verifyNodeStarted();
            this.verifyMaster();
            this.verifyMigrationParticipant();
            this.verifyClusterState();
            this.applyCompletedMigrations();
            this.verifyPartitionStateVersion();
        }
        catch (Exception e) {
            this.onMigrationComplete();
            throw e;
        }
    }

    private void verifyNodeStarted() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        this.nodeStartCompleted = nodeEngine.getNode().getNodeExtension().isStartCompleted();
        if (!this.nodeStartCompleted) {
            throw new IllegalStateException("Migration operation is received before startup is completed. Sender: " + this.getCallerAddress());
        }
    }

    private void applyCompletedMigrations() {
        if (this.completedMigrations == null || this.completedMigrations.isEmpty()) {
            return;
        }
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        if (!partitionService.applyCompletedMigrations(this.completedMigrations, this.migrationInfo.getMaster())) {
            throw new PartitionStateVersionMismatchException(this.partitionStateVersion, partitionService.getPartitionStateVersion());
        }
        if (partitionService.getMigrationManager().isFinalizingMigrationRegistered(this.migrationInfo.getPartitionId())) {
            throw new RetryableHazelcastException("There is a scheduled FinalizeMigrationOperation for the same partition => " + this.migrationInfo);
        }
    }

    private void verifyPartitionStateVersion() {
        InternalPartitionService partitionService = (InternalPartitionService)this.getService();
        int localPartitionStateVersion = partitionService.getPartitionStateVersion();
        if (this.partitionStateVersion != localPartitionStateVersion) {
            if (this.getNodeEngine().getThisAddress().equals(this.migrationInfo.getMaster())) {
                return;
            }
            throw new PartitionStateVersionMismatchException(this.partitionStateVersion, localPartitionStateVersion);
        }
    }

    final void verifyMaster() {
        NodeEngine nodeEngine = this.getNodeEngine();
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        Address masterAddress = nodeEngine.getMasterAddress();
        if (!this.migrationInfo.getMaster().equals(masterAddress)) {
            throw new IllegalStateException("Migration initiator is not master node! => " + this.toString());
        }
        if (!service.isMemberMaster(this.migrationInfo.getMaster())) {
            throw new RetryableHazelcastException("Migration initiator is not the master node known by migration system!");
        }
        if (this.getMigrationParticipantType() == InternalMigrationListener.MigrationParticipant.SOURCE && !service.isMemberMaster(this.getCallerAddress())) {
            throw new IllegalStateException("Caller is not master node! => " + this.toString());
        }
    }

    private void verifyMigrationParticipant() {
        Member localMember = this.getNodeEngine().getLocalMember();
        if (this.getMigrationParticipantType() == InternalMigrationListener.MigrationParticipant.SOURCE) {
            if (this.migrationInfo.getSourceCurrentReplicaIndex() == 0 && !this.migrationInfo.getSource().isIdentical(localMember)) {
                throw new IllegalStateException(localMember + " is the migration source but has a different identity! Migration: " + this.migrationInfo);
            }
            this.verifyPartitionOwner();
            this.verifyExistingDestination();
        } else if (this.getMigrationParticipantType() == InternalMigrationListener.MigrationParticipant.DESTINATION && !this.migrationInfo.getDestination().isIdentical(localMember)) {
            throw new IllegalStateException(localMember + " is the migration destination but has a different identity! Migration: " + this.migrationInfo);
        }
    }

    private void verifyPartitionOwner() {
        InternalPartition partition = this.getPartition();
        PartitionReplica owner = partition.getOwnerReplicaOrNull();
        if (owner == null) {
            throw new RetryableHazelcastException("Cannot migrate at the moment! Owner of the partition is null => " + this.migrationInfo);
        }
        if (!owner.isIdentical(this.getNodeEngine().getLocalMember())) {
            throw new RetryableHazelcastException("Owner of partition is not this node! => " + this.toString());
        }
    }

    final void verifyExistingDestination() {
        PartitionReplica destination = this.migrationInfo.getDestination();
        MemberImpl target = this.getNodeEngine().getClusterService().getMember(destination.address(), destination.uuid());
        if (target == null) {
            throw new TargetNotMemberException("Destination of migration could not be found! => " + this.toString());
        }
    }

    private void verifyClusterState() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        ClusterState clusterState = nodeEngine.getClusterService().getClusterState();
        if (!clusterState.isMigrationAllowed()) {
            throw new IllegalStateException("Cluster state does not allow migrations! " + (Object)((Object)clusterState));
        }
    }

    void setActiveMigration() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        MigrationManager migrationManager = partitionService.getMigrationManager();
        MigrationInfo currentActiveMigration = migrationManager.setActiveMigration(this.migrationInfo);
        if (currentActiveMigration != null) {
            if (this.migrationInfo.equals(currentActiveMigration)) {
                this.migrationInfo = currentActiveMigration;
                return;
            }
            throw new RetryableHazelcastException("Cannot set active migration to " + this.migrationInfo + ". Current active migration is " + currentActiveMigration);
        }
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        if (!partitionStateManager.trySetMigratingFlag(this.migrationInfo.getPartitionId())) {
            throw new RetryableHazelcastException("Cannot set migrating flag, probably previous migration's finalization is not completed yet.");
        }
    }

    void onMigrationStart() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        InternalMigrationListener migrationListener = partitionService.getInternalMigrationListener();
        migrationListener.onMigrationStart(this.getMigrationParticipantType(), this.migrationInfo);
    }

    void onMigrationComplete() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        InternalMigrationListener migrationListener = partitionService.getInternalMigrationListener();
        migrationListener.onMigrationComplete(this.getMigrationParticipantType(), this.migrationInfo, this.success);
    }

    void executeBeforeMigrations() throws Exception {
        PartitionMigrationEvent event = this.getMigrationEvent();
        Throwable t = null;
        for (MigrationAwareService service : this.getMigrationAwareServices()) {
            try {
                service.beforeMigration(event);
            }
            catch (Throwable e) {
                this.getLogger().warning("Error while executing beforeMigration()", e);
                t = e;
            }
        }
        if (t != null) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected abstract PartitionMigrationEvent getMigrationEvent();

    protected abstract InternalMigrationListener.MigrationParticipant getMigrationParticipantType();

    InternalPartition getPartition() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        return partitionService.getPartitionStateManager().getPartitionImpl(this.migrationInfo.getPartitionId());
    }

    public MigrationInfo getMigrationInfo() {
        return this.migrationInfo;
    }

    @Override
    public Object getResponse() {
        return this.success;
    }

    @Override
    public final boolean validatesTarget() {
        return false;
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        if (!this.migrationInfo.isValid()) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public void logError(Throwable e) {
        ILogger logger = this.getLogger();
        if (e instanceof PartitionStateVersionMismatchException) {
            if (logger.isFineEnabled()) {
                logger.fine(e.getMessage(), e);
            } else {
                logger.info(e.getMessage());
            }
            return;
        }
        if (!this.nodeStartCompleted && e instanceof IllegalStateException) {
            logger.warning(e.getMessage());
            if (logger.isFineEnabled()) {
                logger.fine(e);
            }
            return;
        }
        super.logError(e);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        Version version = out.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.migrationInfo);
        } else {
            this.migrationInfo.writeData(out);
        }
        out.writeInt(this.partitionStateVersion);
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            int len = this.completedMigrations.size();
            out.writeInt(len);
            for (MigrationInfo migrationInfo : this.completedMigrations) {
                out.writeObject(migrationInfo);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        Version version = in.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            this.migrationInfo = (MigrationInfo)in.readObject();
        } else {
            this.migrationInfo = new MigrationInfo();
            this.migrationInfo.readData(in);
        }
        this.partitionStateVersion = in.readInt();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            int len = in.readInt();
            this.completedMigrations = new ArrayList<MigrationInfo>(len);
            for (int i = 0; i < len; ++i) {
                MigrationInfo migrationInfo = (MigrationInfo)in.readObject();
                this.completedMigrations.add(migrationInfo);
            }
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", migration=").append(this.migrationInfo);
    }
}

