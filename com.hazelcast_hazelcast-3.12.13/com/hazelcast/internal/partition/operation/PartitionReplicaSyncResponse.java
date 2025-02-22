/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.ReplicaErrorLogger;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

@SuppressFBWarnings(value={"EI_EXPOSE_REP"})
public class PartitionReplicaSyncResponse
extends AbstractPartitionOperation
implements PartitionAwareOperation,
BackupOperation,
UrgentSystemOperation,
AllowedDuringPassiveState,
Versioned,
TargetAware {
    private Collection<Operation> operations;
    private ServiceNamespace namespace;
    private long[] versions;

    public PartitionReplicaSyncResponse() {
    }

    public PartitionReplicaSyncResponse(Collection<Operation> operations, ServiceNamespace namespace, long[] versions) {
        this.operations = operations;
        this.namespace = namespace;
        this.versions = versions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine = this.getNodeEngine();
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        InternalPartitionImpl partition = partitionStateManager.getPartitionImpl(partitionId);
        int currentReplicaIndex = partition.getReplicaIndex(PartitionReplica.from(nodeEngine.getLocalMember()));
        try {
            if (replicaIndex == currentReplicaIndex) {
                this.executeOperations();
            } else {
                this.nodeNotOwnsBackup(partition);
            }
            if (this.operations != null) {
                this.operations.clear();
            }
        }
        finally {
            this.postProcessReplicaSync(partitionService, currentReplicaIndex);
        }
    }

    private void postProcessReplicaSync(InternalPartitionServiceImpl partitionService, int currentReplicaIndex) {
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
        if (replicaIndex == currentReplicaIndex) {
            replicaManager.finalizeReplicaSync(partitionId, replicaIndex, this.namespace, this.versions);
        } else {
            replicaManager.clearReplicaSyncRequest(partitionId, this.namespace, replicaIndex);
            if (currentReplicaIndex < 0) {
                replicaManager.clearPartitionReplicaVersions(partitionId, this.namespace);
            }
        }
    }

    private void nodeNotOwnsBackup(InternalPartitionImpl partition) {
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        NodeEngine nodeEngine = this.getNodeEngine();
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            int currentReplicaIndex = partition.getReplicaIndex(PartitionReplica.from(nodeEngine.getLocalMember()));
            logger.finest("This node is not backup replica of partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + " anymore. current replicaIndex=" + currentReplicaIndex);
        }
        if (this.operations != null) {
            PartitionReplica replica = partition.getReplica(replicaIndex);
            MemberImpl targetMember = null;
            if (replica != null) {
                ClusterServiceImpl clusterService = (ClusterServiceImpl)nodeEngine.getClusterService();
                targetMember = clusterService.getMember(replica.address(), replica.uuid());
            }
            WrongTargetException throwable = new WrongTargetException(nodeEngine.getLocalMember(), targetMember, partitionId, replicaIndex, this.getClass().getName());
            for (Operation op : this.operations) {
                this.prepareOperation(op);
                this.onOperationFailure(op, throwable);
            }
        }
    }

    private void executeOperations() {
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        if (this.operations != null && !this.operations.isEmpty()) {
            this.logApplyReplicaSync(partitionId, replicaIndex);
            for (Operation op : this.operations) {
                this.prepareOperation(op);
                try {
                    op.beforeRun();
                    op.run();
                    op.afterRun();
                }
                catch (Throwable e) {
                    this.onOperationFailure(op, e);
                    this.logException(op, e);
                }
            }
        } else {
            this.logEmptyTaskList(partitionId, replicaIndex);
        }
    }

    private void prepareOperation(Operation op) {
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        NodeEngine nodeEngine = this.getNodeEngine();
        ILogger opLogger = nodeEngine.getLogger(op.getClass());
        OperationResponseHandler responseHandler = OperationResponseHandlerFactory.createErrorLoggingResponseHandler(opLogger);
        op.setNodeEngine(nodeEngine).setPartitionId(partitionId).setReplicaIndex(replicaIndex).setOperationResponseHandler(responseHandler);
    }

    private void logEmptyTaskList(int partitionId, int replicaIndex) {
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("No data available for replica sync, partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespace=" + this.namespace + ", versions=" + Arrays.toString(this.versions));
        }
    }

    private void logException(Operation op, Throwable e) {
        Level level;
        ILogger logger = this.getLogger();
        NodeEngine nodeEngine = this.getNodeEngine();
        Level level2 = level = nodeEngine.isRunning() ? Level.WARNING : Level.FINEST;
        if (logger.isLoggable(level)) {
            logger.log(level, "While executing " + op, e);
        }
    }

    private void logApplyReplicaSync(int partitionId, int replicaIndex) {
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("Applying replica sync for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespace=" + this.namespace + ", versions=" + Arrays.toString(this.versions));
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
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public void onExecutionFailure(Throwable e) {
        if (this.operations != null) {
            for (Operation op : this.operations) {
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
    public void logError(Throwable e) {
        ReplicaErrorLogger.log(e, this.getLogger());
    }

    @Override
    public void setTarget(Address address) {
        if (this.operations != null) {
            for (Operation op : this.operations) {
                if (!(op instanceof TargetAware)) continue;
                ((TargetAware)((Object)op)).setTarget(address);
            }
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeObject(this.namespace);
        out.writeLongArray(this.versions);
        int size = this.operations != null ? this.operations.size() : 0;
        out.writeInt(size);
        if (size > 0) {
            for (Operation task : this.operations) {
                out.writeObject(task);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.namespace = (ServiceNamespace)in.readObject();
        this.versions = in.readLongArray();
        int size = in.readInt();
        if (size > 0) {
            this.operations = new ArrayList<Operation>(size);
            for (int i = 0; i < size; ++i) {
                Operation op = (Operation)in.readObject();
                this.operations.add(op);
            }
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", namespace=").append(this.namespace);
        sb.append(", versions=").append(Arrays.toString(this.versions));
    }

    @Override
    public int getId() {
        return 12;
    }
}

