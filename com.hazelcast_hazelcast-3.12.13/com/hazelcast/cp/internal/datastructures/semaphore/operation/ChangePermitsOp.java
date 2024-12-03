/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreService;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AbstractSemaphoreOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.UUID;

public class ChangePermitsOp
extends AbstractSemaphoreOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private int permits;

    public ChangePermitsOp() {
    }

    public ChangePermitsOp(String name, long sessionId, long threadId, UUID invocationUid, int permits) {
        super(name, sessionId, threadId, invocationUid);
        this.permits = permits;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSemaphoreService service = (RaftSemaphoreService)this.getService();
        return service.changePermits(groupId, commitIndex, this.name, this.getSemaphoreEndpoint(), this.invocationUid, this.permits);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return this.sessionId != -1L;
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
    public int getId() {
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.permits);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.permits = in.readInt();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", permits=").append(this.permits);
    }
}

