/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.proxy;

import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.datastructures.lock.operation.GetLockOwnershipStateOp;
import com.hazelcast.cp.internal.datastructures.lock.operation.LockOp;
import com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp;
import com.hazelcast.cp.internal.datastructures.lock.operation.UnlockOp;
import com.hazelcast.cp.internal.datastructures.lock.proxy.AbstractRaftFencedLockProxy;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.cp.internal.session.ProxySessionManagerService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import java.util.UUID;

public class RaftFencedLockProxy
extends AbstractRaftFencedLockProxy {
    private final RaftInvocationManager invocationManager;

    public RaftFencedLockProxy(NodeEngine nodeEngine, RaftGroupId groupId, String proxyName, String objectName) {
        super((ProxySessionManagerService)nodeEngine.getService("hz:raft:proxySessionManagerService"), groupId, proxyName, objectName);
        RaftService service = (RaftService)nodeEngine.getService("hz:core:raft");
        this.invocationManager = service.getInvocationManager();
    }

    @Override
    protected final InternalCompletableFuture<Long> doLock(long sessionId, long threadId, UUID invocationUid) {
        return this.invoke(new LockOp(this.objectName, sessionId, threadId, invocationUid));
    }

    @Override
    protected final InternalCompletableFuture<Long> doTryLock(long sessionId, long threadId, UUID invocationUid, long timeoutMillis) {
        return this.invoke(new TryLockOp(this.objectName, sessionId, threadId, invocationUid, timeoutMillis));
    }

    @Override
    protected final InternalCompletableFuture<Boolean> doUnlock(long sessionId, long threadId, UUID invocationUid) {
        return this.invoke(new UnlockOp(this.objectName, sessionId, threadId, invocationUid));
    }

    @Override
    protected final InternalCompletableFuture<RaftLockOwnershipState> doGetLockOwnershipState() {
        return this.invoke(new GetLockOwnershipStateOp(this.objectName));
    }

    private <T> InternalCompletableFuture<T> invoke(RaftOp op) {
        return this.invocationManager.invoke(this.groupId, op);
    }

    @Override
    public void destroy() {
        try {
            this.invocationManager.invoke(this.groupId, new DestroyRaftObjectOp(this.getServiceName(), this.objectName)).join();
        }
        finally {
            super.destroy();
        }
    }
}

