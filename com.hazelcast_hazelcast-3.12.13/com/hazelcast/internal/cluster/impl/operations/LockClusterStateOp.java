/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.ClusterStateChange;
import com.hazelcast.internal.cluster.impl.ClusterStateManager;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class LockClusterStateOp
extends Operation
implements AllowedDuringPassiveState,
UrgentSystemOperation,
IdentifiedDataSerializable,
Versioned {
    private ClusterStateChange stateChange;
    private Address initiator;
    private String txnId;
    private long leaseTime;
    private int memberListVersion;
    private int partitionStateVersion;

    public LockClusterStateOp() {
    }

    public LockClusterStateOp(ClusterStateChange stateChange, Address initiator, String txnId, long leaseTime, int memberListVersion, int partitionStateVersion) {
        this.stateChange = stateChange;
        this.initiator = initiator;
        this.txnId = txnId;
        this.leaseTime = leaseTime;
        this.memberListVersion = memberListVersion;
        this.partitionStateVersion = partitionStateVersion;
    }

    @Override
    public void beforeRun() throws Exception {
        if (this.stateChange == null) {
            throw new IllegalArgumentException("Invalid null cluster state");
        }
        this.stateChange.validate();
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        ClusterStateManager clusterStateManager = service.getClusterStateManager();
        ClusterState state = clusterStateManager.getState();
        if (state == ClusterState.IN_TRANSITION) {
            this.getLogger().info("Extending cluster state lock. Initiator: " + this.initiator + ", lease-time: " + this.leaseTime);
        } else {
            this.getLogger().info("Locking cluster state. Initiator: " + this.initiator + ", lease-time: " + this.leaseTime);
        }
        clusterStateManager.lockClusterState(this.stateChange, this.initiator, this.txnId, this.leaseTime, this.memberListVersion, this.partitionStateVersion);
    }

    @Override
    public void logError(Throwable e) {
        if (e instanceof TransactionException || e instanceof IllegalStateException) {
            this.getLogger().severe(e.getMessage());
        } else {
            super.logError(e);
        }
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public String getServiceName() {
        return "hz:core:clusterService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.stateChange);
        this.initiator.writeData(out);
        out.writeUTF(this.txnId);
        out.writeLong(this.leaseTime);
        out.writeInt(this.partitionStateVersion);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeInt(this.memberListVersion);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.stateChange = (ClusterStateChange)in.readObject();
        this.initiator = new Address();
        this.initiator.readData(in);
        this.txnId = in.readUTF();
        this.leaseTime = in.readLong();
        this.partitionStateVersion = in.readInt();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.memberListVersion = in.readInt();
        }
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 15;
    }
}

