/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreService;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AbstractSemaphoreOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.UUID;

public class ReleasePermitsOp
extends AbstractSemaphoreOp
implements IndeterminateOperationStateAware {
    private int permits;

    public ReleasePermitsOp() {
    }

    public ReleasePermitsOp(String name, long sessionId, long threadId, UUID invocationUid, int permits) {
        super(name, sessionId, threadId, invocationUid);
        this.permits = permits;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSemaphoreService service = (RaftSemaphoreService)this.getService();
        service.releasePermits(groupId, commitIndex, this.name, this.getSemaphoreEndpoint(), this.invocationUid, this.permits);
        return true;
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return this.sessionId != -1L;
    }

    @Override
    public int getId() {
        return 10;
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

