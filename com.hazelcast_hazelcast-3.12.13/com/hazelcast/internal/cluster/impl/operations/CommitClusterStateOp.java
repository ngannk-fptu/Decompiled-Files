/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.ClusterStateChange;
import com.hazelcast.internal.cluster.impl.ClusterStateManager;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class CommitClusterStateOp
extends Operation
implements AllowedDuringPassiveState,
UrgentSystemOperation,
IdentifiedDataSerializable {
    private ClusterStateChange stateChange;
    private Address initiator;
    private String txnId;
    private boolean isTransient;

    public CommitClusterStateOp() {
    }

    public CommitClusterStateOp(ClusterStateChange stateChange, Address initiator, String txnId, boolean isTransient) {
        this.stateChange = stateChange;
        this.initiator = initiator;
        this.txnId = txnId;
        this.isTransient = isTransient;
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
        this.getLogger().info(String.format("Changing cluster state from %s to %s, initiator: %s, transient: %s", clusterStateManager.stateToString(), this.stateChange, this.initiator, this.isTransient));
        clusterStateManager.commitClusterState(this.stateChange, this.initiator, this.txnId, this.isTransient);
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
    public Object getResponse() {
        return Boolean.TRUE;
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
        out.writeBoolean(this.isTransient);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.stateChange = (ClusterStateChange)in.readObject();
        this.initiator = new Address();
        this.initiator.readData(in);
        this.txnId = in.readUTF();
        this.isTransient = in.readBoolean();
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 10;
    }
}

