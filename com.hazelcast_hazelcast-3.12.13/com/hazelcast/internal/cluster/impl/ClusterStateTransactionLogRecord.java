/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.cluster.impl.ClusterStateChange;
import com.hazelcast.internal.cluster.impl.operations.CommitClusterStateOp;
import com.hazelcast.internal.cluster.impl.operations.LockClusterStateOp;
import com.hazelcast.internal.cluster.impl.operations.RollbackClusterStateOp;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TargetAwareTransactionLogRecord;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class ClusterStateTransactionLogRecord
implements TargetAwareTransactionLogRecord,
Versioned {
    ClusterStateChange stateChange;
    Address initiator;
    Address target;
    String txnId;
    long leaseTime;
    int memberListVersion;
    int partitionStateVersion;
    boolean isTransient;

    public ClusterStateTransactionLogRecord() {
    }

    public ClusterStateTransactionLogRecord(ClusterStateChange stateChange, Address initiator, Address target, String txnId, long leaseTime, int memberListVersion, int partitionStateVersion, boolean isTransient) {
        this.memberListVersion = memberListVersion;
        Preconditions.checkNotNull(stateChange);
        Preconditions.checkNotNull(initiator);
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(txnId);
        Preconditions.checkPositive(leaseTime, "Lease time should be positive!");
        this.stateChange = stateChange;
        this.initiator = initiator;
        this.target = target;
        this.txnId = txnId;
        this.leaseTime = leaseTime;
        this.partitionStateVersion = partitionStateVersion;
        this.isTransient = isTransient;
    }

    @Override
    public Object getKey() {
        return null;
    }

    @Override
    public Operation newPrepareOperation() {
        return new LockClusterStateOp(this.stateChange, this.initiator, this.txnId, this.leaseTime, this.memberListVersion, this.partitionStateVersion);
    }

    @Override
    public Operation newCommitOperation() {
        return new CommitClusterStateOp(this.stateChange, this.initiator, this.txnId, this.isTransient);
    }

    @Override
    public void onCommitSuccess() {
    }

    @Override
    public void onCommitFailure() {
    }

    @Override
    public Operation newRollbackOperation() {
        return new RollbackClusterStateOp(this.initiator, this.txnId);
    }

    @Override
    public Address getTarget() {
        return this.target;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.stateChange);
        out.writeObject(this.initiator);
        out.writeObject(this.target);
        out.writeUTF(this.txnId);
        out.writeLong(this.leaseTime);
        out.writeInt(this.partitionStateVersion);
        out.writeBoolean(this.isTransient);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeInt(this.memberListVersion);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.stateChange = (ClusterStateChange)in.readObject();
        this.initiator = (Address)in.readObject();
        this.target = (Address)in.readObject();
        this.txnId = in.readUTF();
        this.leaseTime = in.readLong();
        this.partitionStateVersion = in.readInt();
        this.isTransient = in.readBoolean();
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
        return 27;
    }
}

