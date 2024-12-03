/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CallerAware;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.lock.AcquireResult;
import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockService;
import com.hazelcast.cp.internal.datastructures.lock.operation.AbstractLockOp;
import com.hazelcast.cp.internal.raft.impl.util.PostponedResponse;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.UUID;

public class TryLockOp
extends AbstractLockOp
implements CallerAware,
IndeterminateOperationStateAware {
    private long timeoutMs;
    private Address callerAddress;
    private long callId;

    public TryLockOp() {
    }

    public TryLockOp(String name, long sessionId, long threadId, UUID invUid, long timeoutMs) {
        super(name, sessionId, threadId, invUid);
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        LockInvocationKey key;
        RaftLockService service = (RaftLockService)this.getService();
        AcquireResult result = service.acquire(groupId, this.name, key = new LockInvocationKey(commitIndex, this.invocationUid, this.callerAddress, this.callId, this.getLockEndpoint()), this.timeoutMs);
        if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
            return PostponedResponse.INSTANCE;
        }
        return result.fence();
    }

    @Override
    public void setCaller(Address callerAddress, long callId) {
        this.callerAddress = callerAddress;
        this.callId = callId;
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeLong(this.timeoutMs);
        out.writeObject(this.callerAddress);
        out.writeLong(this.callId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.timeoutMs = in.readLong();
        this.callerAddress = (Address)in.readObject();
        this.callId = in.readLong();
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", timeoutMs=").append(this.timeoutMs);
        sb.append(", callerAddress=").append(this.callerAddress);
        sb.append(", callId=").append(this.callId);
    }
}

