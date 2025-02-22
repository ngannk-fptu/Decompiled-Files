/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.operation;

import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.lock.LockEndpoint;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockDataSerializerHook;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.UUID;

abstract class AbstractLockOp
extends RaftOp
implements IdentifiedDataSerializable {
    String name;
    long sessionId;
    long threadId;
    UUID invocationUid;

    AbstractLockOp() {
    }

    AbstractLockOp(String name, long sessionId, long threadId, UUID invocationUid) {
        this.name = name;
        this.sessionId = sessionId;
        this.threadId = threadId;
        this.invocationUid = invocationUid;
    }

    LockEndpoint getLockEndpoint() {
        return new LockEndpoint(this.sessionId, this.threadId);
    }

    @Override
    public final String getServiceName() {
        return "hz:raft:lockService";
    }

    @Override
    public int getFactoryId() {
        return RaftLockDataSerializerHook.F_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.sessionId);
        out.writeLong(this.threadId);
        UUIDSerializationUtil.writeUUID(out, this.invocationUid);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.sessionId = in.readLong();
        this.threadId = in.readLong();
        this.invocationUid = UUIDSerializationUtil.readUUID(in);
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", name=").append(this.name);
        sb.append(", sessionId=").append(this.sessionId);
        sb.append(", threadId=").append(this.threadId);
        sb.append(", invocationUid=").append(this.invocationUid);
    }
}

