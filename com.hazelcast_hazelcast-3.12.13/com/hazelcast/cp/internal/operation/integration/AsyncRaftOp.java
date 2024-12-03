/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.RaftSystemOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public abstract class AsyncRaftOp
extends Operation
implements IdentifiedDataSerializable,
RaftSystemOperation {
    protected CPGroupId groupId;
    protected CPMember target;

    AsyncRaftOp() {
    }

    AsyncRaftOp(CPGroupId groupId) {
        this.groupId = groupId;
    }

    public final Operation setTargetMember(CPMember target) {
        this.target = target;
        return this;
    }

    @Override
    public final boolean returnsResponse() {
        return false;
    }

    @Override
    public final Object getResponse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public final boolean validatesTarget() {
        return false;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.groupId);
        out.writeObject(this.target);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.groupId = (CPGroupId)in.readObject();
        this.target = (CPMember)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }
}

