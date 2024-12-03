/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
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
import java.io.IOException;

public class RollbackClusterStateOp
extends Operation
implements AllowedDuringPassiveState,
UrgentSystemOperation,
IdentifiedDataSerializable {
    private Address initiator;
    private String txnId;
    private boolean response;

    public RollbackClusterStateOp() {
    }

    public RollbackClusterStateOp(Address initiator, String txnId) {
        this.initiator = initiator;
        this.txnId = txnId;
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        ClusterStateManager clusterStateManager = service.getClusterStateManager();
        this.getLogger().info("Rolling back cluster state! Initiator: " + this.initiator);
        this.response = clusterStateManager.rollbackClusterState(this.txnId);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public String getServiceName() {
        return "hz:core:clusterService";
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        this.initiator.writeData(out);
        out.writeUTF(this.txnId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.initiator = new Address();
        this.initiator.readData(in);
        this.txnId = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 23;
    }
}

