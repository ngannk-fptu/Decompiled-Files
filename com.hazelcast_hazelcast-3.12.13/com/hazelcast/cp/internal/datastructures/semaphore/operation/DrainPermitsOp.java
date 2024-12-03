/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreService;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AbstractSemaphoreOp;
import java.util.UUID;

public class DrainPermitsOp
extends AbstractSemaphoreOp
implements IndeterminateOperationStateAware {
    public DrainPermitsOp() {
    }

    public DrainPermitsOp(String name, long sessionId, long threadId, UUID invocationUid) {
        super(name, sessionId, threadId, invocationUid);
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSemaphoreService service = (RaftSemaphoreService)this.getService();
        return service.drainPermits(groupId, this.name, commitIndex, this.getSemaphoreEndpoint(), this.invocationUid);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return this.sessionId != -1L;
    }

    @Override
    public int getId() {
        return 8;
    }
}

