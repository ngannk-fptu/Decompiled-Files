/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.operation;

import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.semaphore.SemaphoreEndpoint;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.UUID;

abstract class AbstractSemaphoreOp
extends RaftOp
implements IdentifiedDataSerializable {
    protected String name;
    protected long sessionId;
    protected long threadId;
    protected UUID invocationUid;

    public AbstractSemaphoreOp() {
    }

    AbstractSemaphoreOp(String name, long sessionId, long threadId, UUID invocationUid) {
        this.name = name;
        this.sessionId = sessionId;
        this.threadId = threadId;
        this.invocationUid = invocationUid;
    }

    SemaphoreEndpoint getSemaphoreEndpoint() {
        return new SemaphoreEndpoint(this.sessionId, this.threadId);
    }

    @Override
    protected String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public int getFactoryId() {
        return RaftSemaphoreDataSerializerHook.F_ID;
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
        sb.append(", name=").append(this.name).append(", threadId=").append(this.threadId).append(", sessionId=").append(this.sessionId).append(", invocationUid=").append(this.invocationUid);
    }
}

