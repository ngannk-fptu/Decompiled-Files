/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockService;
import com.hazelcast.cp.internal.datastructures.lock.operation.AbstractLockOp;
import java.util.UUID;

public class UnlockOp
extends AbstractLockOp
implements IndeterminateOperationStateAware {
    public UnlockOp() {
    }

    public UnlockOp(String name, long sessionId, long threadId, UUID invUid) {
        super(name, sessionId, threadId, invUid);
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftLockService service = (RaftLockService)this.getService();
        return service.release(groupId, commitIndex, this.name, this.getLockEndpoint(), this.invocationUid);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getId() {
        return 8;
    }
}

