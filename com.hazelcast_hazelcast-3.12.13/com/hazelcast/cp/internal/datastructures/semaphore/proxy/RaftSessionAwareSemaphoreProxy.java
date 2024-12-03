/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.proxy;

import com.hazelcast.core.ISemaphore;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AcquirePermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AvailablePermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.DrainPermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.InitSemaphoreOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.ReleasePermitsOp;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.cp.internal.session.ProxySessionManagerService;
import com.hazelcast.cp.internal.session.SessionAwareProxy;
import com.hazelcast.cp.internal.session.SessionExpiredException;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.UuidUtil;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RaftSessionAwareSemaphoreProxy
extends SessionAwareProxy
implements ISemaphore {
    public static final int DRAIN_SESSION_ACQ_COUNT = 1024;
    private final RaftInvocationManager invocationManager;
    private final String proxyName;
    private final String objectName;

    public RaftSessionAwareSemaphoreProxy(NodeEngine nodeEngine, RaftGroupId groupId, String proxyName, String objectName) {
        super((ProxySessionManagerService)nodeEngine.getService("hz:raft:proxySessionManagerService"), groupId);
        RaftService service = (RaftService)nodeEngine.getService("hz:core:raft");
        this.invocationManager = service.getInvocationManager();
        this.proxyName = proxyName;
        this.objectName = objectName;
    }

    @Override
    public boolean init(int permits) {
        Preconditions.checkNotNegative(permits, "Permits must be non-negative!");
        return (Boolean)this.invocationManager.invoke(this.groupId, new InitSemaphoreOp(this.objectName, permits)).join();
    }

    @Override
    public void acquire() {
        this.acquire(1);
    }

    @Override
    public void acquire(int permits) {
        Preconditions.checkPositive(permits, "Permits must be positive!");
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        while (true) {
            long sessionId = this.acquireSession(permits);
            AcquirePermitsOp op = new AcquirePermitsOp(this.objectName, sessionId, threadId, invocationUid, permits, -1L);
            try {
                this.invocationManager.invoke(this.groupId, op).join();
                return;
            }
            catch (SessionExpiredException e) {
                this.invalidateSession(sessionId);
                continue;
            }
            break;
        }
    }

    @Override
    public boolean tryAcquire() {
        return this.tryAcquire(1);
    }

    @Override
    public boolean tryAcquire(int permits) {
        return this.tryAcquire(permits, 0L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return this.tryAcquire(1, timeout, unit);
    }

    @Override
    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
        Preconditions.checkPositive(permits, "Permits must be positive!");
        long timeoutMs = Math.max(0L, unit.toMillis(timeout));
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        while (true) {
            long start = Clock.currentTimeMillis();
            long sessionId = this.acquireSession(permits);
            AcquirePermitsOp op = new AcquirePermitsOp(this.objectName, sessionId, threadId, invocationUid, permits, timeoutMs);
            try {
                InternalCompletableFuture f = this.invocationManager.invoke(this.groupId, op);
                boolean acquired = (Boolean)f.join();
                if (!acquired) {
                    this.releaseSession(sessionId, permits);
                }
                return acquired;
            }
            catch (SessionExpiredException e) {
                this.invalidateSession(sessionId);
                if ((timeoutMs -= Clock.currentTimeMillis() - start) > 0L) continue;
                return false;
            }
            break;
        }
    }

    @Override
    public void release() {
        this.release(1);
    }

    @Override
    public void release(int permits) {
        Preconditions.checkPositive(permits, "Permits must be positive!");
        long sessionId = this.getSession();
        if (sessionId == -1L) {
            throw this.newIllegalStateException(null);
        }
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        ReleasePermitsOp op = new ReleasePermitsOp(this.objectName, sessionId, threadId, invocationUid, permits);
        try {
            this.invocationManager.invoke(this.groupId, op).join();
        }
        catch (SessionExpiredException e) {
            this.invalidateSession(sessionId);
            throw this.newIllegalStateException(e);
        }
        finally {
            this.releaseSession(sessionId, permits);
        }
    }

    @Override
    public int availablePermits() {
        return (Integer)this.invocationManager.invoke(this.groupId, new AvailablePermitsOp(this.objectName)).join();
    }

    @Override
    public int drainPermits() {
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        while (true) {
            long sessionId = this.acquireSession(1024);
            DrainPermitsOp op = new DrainPermitsOp(this.objectName, sessionId, threadId, invocationUid);
            try {
                InternalCompletableFuture future = this.invocationManager.invoke(this.groupId, op);
                int count = (Integer)future.join();
                this.releaseSession(sessionId, 1024 - count);
                return count;
            }
            catch (SessionExpiredException e) {
                this.invalidateSession(sessionId);
                continue;
            }
            break;
        }
    }

    @Override
    public void reducePermits(int reduction) {
        Preconditions.checkNotNegative(reduction, "Reduction must be non-negative!");
        if (reduction == 0) {
            return;
        }
        long sessionId = this.acquireSession();
        if (sessionId == -1L) {
            throw this.newIllegalStateException(null);
        }
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        try {
            ChangePermitsOp op = new ChangePermitsOp(this.objectName, sessionId, threadId, invocationUid, -reduction);
            this.invocationManager.invoke(this.groupId, op).join();
        }
        catch (SessionExpiredException e) {
            this.invalidateSession(sessionId);
            throw this.newIllegalStateException(e);
        }
        finally {
            this.releaseSession(sessionId);
        }
    }

    @Override
    public void increasePermits(int increase) {
        Preconditions.checkNotNegative(increase, "Increase must be non-negative!");
        if (increase == 0) {
            return;
        }
        long sessionId = this.acquireSession();
        if (sessionId == -1L) {
            throw this.newIllegalStateException(null);
        }
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        try {
            ChangePermitsOp op = new ChangePermitsOp(this.objectName, sessionId, threadId, invocationUid, increase);
            this.invocationManager.invoke(this.groupId, op).join();
        }
        catch (SessionExpiredException e) {
            this.invalidateSession(sessionId);
            throw this.newIllegalStateException(e);
        }
        finally {
            this.releaseSession(sessionId);
        }
    }

    private IllegalStateException newIllegalStateException(SessionExpiredException e) {
        return new IllegalStateException("No valid session!", e);
    }

    @Override
    public String getName() {
        return this.proxyName;
    }

    @Override
    public String getPartitionKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public void destroy() {
        this.invocationManager.invoke(this.groupId, new DestroyRaftObjectOp(this.getServiceName(), this.objectName)).join();
    }
}

