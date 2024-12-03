/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.session.RaftSessionService;
import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CloseSessionOp
extends RaftOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private long sessionId;

    public CloseSessionOp() {
    }

    public CloseSessionOp(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSessionService service = (RaftSessionService)this.getService();
        return service.closeSession(groupId, this.sessionId);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public String getServiceName() {
        return "hz:core:raftSession";
    }

    @Override
    public int getFactoryId() {
        return RaftSessionServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.sessionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.sessionId = in.readLong();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", sessionId=").append(this.sessionId);
    }
}

