/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.ReplicaFragmentMigrationState;
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.operation.BaseMigrationOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MigrationOperation
extends BaseMigrationOperation
implements TargetAware,
Versioned {
    private static final OperationResponseHandler ERROR_RESPONSE_HANDLER = new OperationResponseHandler(){

        public void sendResponse(Operation op, Object obj) {
            throw new HazelcastException("Migration operations can not send response!");
        }
    };
    private ReplicaFragmentMigrationState fragmentMigrationState;
    private boolean firstFragment;
    private boolean lastFragment;
    private Throwable failureReason;

    public MigrationOperation() {
    }

    public MigrationOperation(MigrationInfo migrationInfo, List<MigrationInfo> completedMigrations, int partitionStateVersion, ReplicaFragmentMigrationState fragmentMigrationState, boolean firstFragment, boolean lastFragment) {
        super(migrationInfo, completedMigrations, partitionStateVersion);
        this.fragmentMigrationState = fragmentMigrationState;
        this.firstFragment = firstFragment;
        this.lastFragment = lastFragment;
        this.setReplicaIndex(migrationInfo.getDestinationNewReplicaIndex());
    }

    @Override
    public void run() throws Exception {
        if (this.firstFragment) {
            this.setActiveMigration();
        }
        try {
            this.checkActiveMigration();
            this.doRun();
        }
        catch (Throwable t) {
            this.logMigrationFailure(t);
            this.failureReason = t;
        }
        finally {
            this.onMigrationComplete();
            if (!this.success) {
                this.onExecutionFailure(this.failureReason);
            }
        }
    }

    private void doRun() {
        if (this.migrationInfo.startProcessing()) {
            try {
                if (this.firstFragment) {
                    this.executeBeforeMigrations();
                }
                for (Operation migrationOperation : this.fragmentMigrationState.getMigrationOperations()) {
                    this.runMigrationOperation(migrationOperation);
                }
                this.success = true;
            }
            catch (Throwable e) {
                this.failureReason = e;
                this.getLogger().severe("Error while executing replication operations " + this.migrationInfo, e);
            }
            finally {
                this.afterMigrate();
            }
        } else {
            this.logMigrationCancelled();
        }
    }

    private void checkActiveMigration() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        MigrationInfo activeMigration = partitionService.getMigrationManager().getActiveMigration();
        if (!this.migrationInfo.equals(activeMigration)) {
            throw new IllegalStateException("Unexpected active migration " + activeMigration + "! First migration fragment should have set active migration to: " + this.migrationInfo);
        }
    }

    private void runMigrationOperation(Operation op) throws Exception {
        this.prepareOperation(op);
        op.beforeRun();
        op.run();
        op.afterRun();
    }

    protected void prepareOperation(Operation op) {
        op.setNodeEngine(this.getNodeEngine()).setPartitionId(this.getPartitionId()).setReplicaIndex(this.getReplicaIndex());
        op.setOperationResponseHandler(ERROR_RESPONSE_HANDLER);
        OperationAccessor.setCallerAddress(op, this.migrationInfo.getSourceAddress());
    }

    private void afterMigrate() {
        ILogger logger = this.getLogger();
        if (this.success) {
            InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
            PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
            int destinationNewReplicaIndex = this.migrationInfo.getDestinationNewReplicaIndex();
            int replicaOffset = Math.max(destinationNewReplicaIndex, 1);
            Map<ServiceNamespace, long[]> namespaceVersions = this.fragmentMigrationState.getNamespaceVersionMap();
            for (Map.Entry<ServiceNamespace, long[]> e : namespaceVersions.entrySet()) {
                ServiceNamespace namespace = e.getKey();
                long[] replicaVersions = e.getValue();
                replicaManager.setPartitionReplicaVersions(this.migrationInfo.getPartitionId(), namespace, replicaVersions, replicaOffset);
                if (!logger.isFinestEnabled()) continue;
                logger.finest("ReplicaVersions are set after migration. partitionId=" + this.migrationInfo.getPartitionId() + " namespace: " + namespace + " replicaVersions=" + Arrays.toString(replicaVersions));
            }
        } else if (logger.isFinestEnabled()) {
            logger.finest("ReplicaVersions are not set since migration failed. partitionId=" + this.migrationInfo.getPartitionId());
        }
        this.migrationInfo.doneProcessing();
    }

    private void logMigrationCancelled() {
        this.getLogger().warning("Migration is cancelled -> " + this.migrationInfo);
    }

    private void logMigrationFailure(Throwable e) {
        ILogger logger = this.getLogger();
        if (e instanceof IllegalStateException) {
            logger.warning(e.getMessage());
        } else {
            logger.warning(e.getMessage(), e);
        }
    }

    @Override
    protected PartitionMigrationEvent getMigrationEvent() {
        return new PartitionMigrationEvent(MigrationEndpoint.DESTINATION, this.migrationInfo.getPartitionId(), this.migrationInfo.getDestinationCurrentReplicaIndex(), this.migrationInfo.getDestinationNewReplicaIndex());
    }

    @Override
    protected InternalMigrationListener.MigrationParticipant getMigrationParticipantType() {
        return InternalMigrationListener.MigrationParticipant.DESTINATION;
    }

    @Override
    public void onExecutionFailure(Throwable e) {
        if (this.fragmentMigrationState == null) {
            return;
        }
        Collection<Operation> tasks = this.fragmentMigrationState.getMigrationOperations();
        if (tasks != null) {
            for (Operation op : tasks) {
                this.prepareOperation(op);
                this.onOperationFailure(op, e);
            }
        }
    }

    private void onOperationFailure(Operation op, Throwable e) {
        try {
            op.onExecutionFailure(e);
        }
        catch (Throwable t) {
            this.getLogger().warning("While calling operation.onFailure(). op: " + op, t);
        }
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    void onMigrationStart() {
        if (this.firstFragment) {
            super.onMigrationStart();
        }
    }

    @Override
    void onMigrationComplete() {
        if (this.lastFragment) {
            super.onMigrationComplete();
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        Version version = out.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.fragmentMigrationState);
        } else {
            this.fragmentMigrationState.writeData(out);
        }
        out.writeBoolean(this.firstFragment);
        out.writeBoolean(this.lastFragment);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        Version version = in.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            this.fragmentMigrationState = (ReplicaFragmentMigrationState)in.readObject();
        } else {
            this.fragmentMigrationState = new ReplicaFragmentMigrationState();
            this.fragmentMigrationState.readData(in);
        }
        this.firstFragment = in.readBoolean();
        this.lastFragment = in.readBoolean();
    }

    @Override
    public void setTarget(Address address) {
        this.fragmentMigrationState.setTarget(address);
    }
}

