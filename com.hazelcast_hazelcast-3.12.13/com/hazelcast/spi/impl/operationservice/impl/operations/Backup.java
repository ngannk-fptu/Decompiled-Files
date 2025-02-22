/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl.operationservice.impl.operations;

import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.ReplicaErrorLogger;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.util.Clock;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;

public final class Backup
extends Operation
implements BackupOperation,
AllowedDuringPassiveState,
IdentifiedDataSerializable {
    private Address originalCaller;
    private ServiceNamespace namespace;
    private long[] replicaVersions;
    private boolean sync;
    private Operation backupOp;
    private Data backupOpData;
    private transient Throwable validationFailure;
    private transient boolean backupOperationInitialized;

    public Backup() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public Backup(Operation backupOp, Address originalCaller, long[] replicaVersions, boolean sync) {
        this.backupOp = backupOp;
        this.originalCaller = originalCaller;
        this.sync = sync;
        this.replicaVersions = replicaVersions;
        if (sync && originalCaller == null) {
            throw new IllegalArgumentException("Sync backup requires original caller address, Backup operation: " + backupOp);
        }
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public Backup(Data backupOpData, Address originalCaller, long[] replicaVersions, boolean sync) {
        this.backupOpData = backupOpData;
        this.originalCaller = originalCaller;
        this.sync = sync;
        this.replicaVersions = replicaVersions;
        if (sync && originalCaller == null) {
            throw new IllegalArgumentException("Sync backup requires original caller address, Backup operation data: " + backupOpData);
        }
    }

    public Operation getBackupOp() {
        return this.backupOp;
    }

    @Override
    public void beforeRun() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        int partitionId = this.getPartitionId();
        InternalPartitionService partitionService = nodeEngine.getPartitionService();
        ILogger logger = this.getLogger();
        this.ensureBackupOperationInitialized();
        PartitionReplicaVersionManager versionManager = partitionService.getPartitionReplicaVersionManager();
        this.namespace = versionManager.getServiceNamespace(this.backupOp);
        if (!nodeEngine.getNode().getNodeExtension().isStartCompleted()) {
            this.validationFailure = new IllegalStateException("Ignoring backup! Backup operation is received before startup is completed.");
            if (logger.isFinestEnabled()) {
                logger.finest(this.validationFailure.getMessage());
            }
            return;
        }
        InternalPartition partition = partitionService.getPartition(partitionId);
        PartitionReplica owner = partition.getReplica(this.getReplicaIndex());
        if (owner == null || !owner.isIdentical(nodeEngine.getLocalMember())) {
            this.validationFailure = new IllegalStateException("Wrong target! " + this.toString() + " cannot be processed! Target should be: " + owner);
            if (logger.isFinestEnabled()) {
                logger.finest(this.validationFailure.getMessage());
            }
            return;
        }
        if (versionManager.isPartitionReplicaVersionStale(this.getPartitionId(), this.namespace, this.replicaVersions, this.getReplicaIndex())) {
            this.validationFailure = new IllegalStateException("Ignoring stale backup with namespace: " + this.namespace + ", versions: " + Arrays.toString(this.replicaVersions));
            if (logger.isFineEnabled()) {
                long[] currentVersions = versionManager.getPartitionReplicaVersions(partitionId, this.namespace);
                logger.fine("Ignoring stale backup! namespace: " + this.namespace + ", Current-versions: " + Arrays.toString(currentVersions) + ", Backup-versions: " + Arrays.toString(this.replicaVersions));
            }
            return;
        }
    }

    private void ensureBackupOperationInitialized() {
        if (!this.backupOperationInitialized) {
            this.backupOperationInitialized = true;
            this.backupOp.setNodeEngine(this.getNodeEngine());
            this.backupOp.setPartitionId(this.getPartitionId());
            this.backupOp.setReplicaIndex(this.getReplicaIndex());
            this.backupOp.setCallerUuid(this.getCallerUuid());
            OperationAccessor.setCallerAddress(this.backupOp, this.getCallerAddress());
            OperationAccessor.setInvocationTime(this.backupOp, Clock.currentTimeMillis());
            this.backupOp.setOperationResponseHandler(OperationResponseHandlerFactory.createEmptyResponseHandler());
        }
    }

    @Override
    public void run() throws Exception {
        if (this.validationFailure != null) {
            this.onExecutionFailure(this.validationFailure);
            return;
        }
        this.ensureBackupOperationInitialized();
        this.backupOp.beforeRun();
        this.backupOp.run();
        this.backupOp.afterRun();
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        PartitionReplicaVersionManager versionManager = nodeEngine.getPartitionService().getPartitionReplicaVersionManager();
        versionManager.updatePartitionReplicaVersions(this.getPartitionId(), this.namespace, this.replicaVersions, this.getReplicaIndex());
    }

    @Override
    public void afterRun() throws Exception {
        if (this.validationFailure != null || !this.sync || this.getCallId() == 0L || this.originalCaller == null) {
            return;
        }
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        long callId = this.getCallId();
        OperationServiceImpl operationService = (OperationServiceImpl)nodeEngine.getOperationService();
        if (nodeEngine.getThisAddress().equals(this.originalCaller)) {
            operationService.getBackupHandler().notifyBackupComplete(callId);
        } else {
            operationService.getOutboundResponseHandler().sendBackupAck(this.getConnection().getEndpointManager(), this.originalCaller, callId, this.backupOp.isUrgent());
        }
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
    public void onExecutionFailure(Throwable e) {
        if (this.backupOp != null) {
            try {
                this.ensureBackupOperationInitialized();
                this.backupOp.onExecutionFailure(e);
            }
            catch (Throwable t) {
                this.getLogger().warning("While calling operation.onFailure(). op: " + this.backupOp, t);
            }
        }
    }

    @Override
    public void logError(Throwable e) {
        if (this.backupOp != null) {
            this.ensureBackupOperationInitialized();
            this.backupOp.logError(e);
        } else {
            ReplicaErrorLogger.log(e, this.getLogger());
        }
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        int k;
        if (this.backupOpData == null) {
            out.writeBoolean(false);
            out.writeObject(this.backupOp);
        } else {
            out.writeBoolean(true);
            out.writeData(this.backupOpData);
        }
        if (this.originalCaller == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            this.originalCaller.writeData(out);
        }
        int replicaVersionCount = 0;
        for (k = 0; k < this.replicaVersions.length; ++k) {
            if (this.replicaVersions[k] == 0L) continue;
            replicaVersionCount = (byte)(k + 1);
        }
        out.writeByte(replicaVersionCount);
        for (k = 0; k < replicaVersionCount; ++k) {
            out.writeLong(this.replicaVersions[k]);
        }
        out.writeBoolean(this.sync);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.backupOp = in.readBoolean() ? (Operation)in.readDataAsObject() : (Operation)in.readObject();
        if (in.readBoolean()) {
            this.originalCaller = new Address();
            this.originalCaller.readData(in);
        }
        this.replicaVersions = new long[6];
        int replicaVersionCount = in.readByte();
        for (int k = 0; k < replicaVersionCount; ++k) {
            this.replicaVersions[k] = in.readLong();
        }
        this.sync = in.readBoolean();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", backupOp=").append(this.backupOp);
        sb.append(", backupOpData=").append(this.backupOpData);
        sb.append(", originalCaller=").append(this.originalCaller);
        sb.append(", version=").append(Arrays.toString(this.replicaVersions));
        sb.append(", sync=").append(this.sync);
    }
}

