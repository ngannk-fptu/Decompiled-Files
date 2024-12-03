/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CallerAware;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireResult;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreService;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AbstractSemaphoreOp;
import com.hazelcast.cp.internal.raft.impl.util.PostponedResponse;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.UUID;

public class AcquirePermitsOp
extends AbstractSemaphoreOp
implements CallerAware,
IndeterminateOperationStateAware {
    private int permits;
    private long timeoutMs;
    private Address callerAddress;
    private long callId;

    public AcquirePermitsOp() {
    }

    public AcquirePermitsOp(String name, long sessionId, long threadId, UUID invocationUid, int permits, long timeoutMs) {
        super(name, sessionId, threadId, invocationUid);
        this.permits = permits;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        AcquireInvocationKey key;
        RaftSemaphoreService service = (RaftSemaphoreService)this.getService();
        AcquireResult result = service.acquirePermits(groupId, this.name, key = new AcquireInvocationKey(commitIndex, this.invocationUid, this.callerAddress, this.callId, this.getSemaphoreEndpoint(), this.permits), this.timeoutMs);
        if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
            return PostponedResponse.INSTANCE;
        }
        return result.status() == AcquireResult.AcquireStatus.SUCCESSFUL;
    }

    @Override
    public void setCaller(Address callerAddress, long callId) {
        this.callerAddress = callerAddress;
        this.callId = callId;
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return this.sessionId != -1L;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.permits);
        out.writeLong(this.timeoutMs);
        out.writeObject(this.callerAddress);
        out.writeLong(this.callId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.permits = in.readInt();
        this.timeoutMs = in.readLong();
        this.callerAddress = (Address)in.readObject();
        this.callId = in.readLong();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", permits=").append(this.permits).append(", timeoutMs=").append(this.timeoutMs).append(", callerAddress=").append(this.callerAddress).append(", callId=").append(this.callId);
    }
}

