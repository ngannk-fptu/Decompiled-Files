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
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import java.util.concurrent.TimeUnit;

public class RaftSessionlessSemaphoreProxy
extends SessionAwareProxy
implements ISemaphore {
    private final RaftInvocationManager invocationManager;
    private final String proxyName;
    private final String objectName;

    public RaftSessionlessSemaphoreProxy(NodeEngine nodeEngine, RaftGroupId groupId, String proxyName, String objectName) {
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
        long clusterWideThreadId = this.getOrCreateUniqueThreadId(this.groupId);
        AcquirePermitsOp op = new AcquirePermitsOp(this.objectName, -1L, clusterWideThreadId, UuidUtil.newUnsecureUUID(), permits, -1L);
        this.invocationManager.invoke(this.groupId, op).join();
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
        long clusterWideThreadId = this.getOrCreateUniqueThreadId(this.groupId);
        long timeoutMs = Math.max(0L, unit.toMillis(timeout));
        AcquirePermitsOp op = new AcquirePermitsOp(this.objectName, -1L, clusterWideThreadId, UuidUtil.newUnsecureUUID(), permits, timeoutMs);
        return (Boolean)this.invocationManager.invoke(this.groupId, op).join();
    }

    @Override
    public void release() {
        this.release(1);
    }

    @Override
    public void release(int permits) {
        Preconditions.checkPositive(permits, "Permits must be positive!");
        long clusterWideThreadId = this.getOrCreateUniqueThreadId(this.groupId);
        ReleasePermitsOp op = new ReleasePermitsOp(this.objectName, -1L, clusterWideThreadId, UuidUtil.newUnsecureUUID(), permits);
        this.invocationManager.invoke(this.groupId, op).join();
    }

    @Override
    public int availablePermits() {
        return (Integer)this.invocationManager.invoke(this.groupId, new AvailablePermitsOp(this.objectName)).join();
    }

    @Override
    public int drainPermits() {
        long clusterWideThreadId = this.getOrCreateUniqueThreadId(this.groupId);
        DrainPermitsOp op = new DrainPermitsOp(this.objectName, -1L, clusterWideThreadId, UuidUtil.newUnsecureUUID());
        return (Integer)this.invocationManager.invoke(this.groupId, op).join();
    }

    @Override
    public void reducePermits(int reduction) {
        Preconditions.checkNotNegative(reduction, "Reduction must be non-negative!");
        if (reduction == 0) {
            return;
        }
        long clusterWideThreadId = this.getOrCreateUniqueThreadId(this.groupId);
        ChangePermitsOp op = new ChangePermitsOp(this.objectName, -1L, clusterWideThreadId, UuidUtil.newUnsecureUUID(), -reduction);
        this.invocationManager.invoke(this.groupId, op).join();
    }

    @Override
    public void increasePermits(int increase) {
        Preconditions.checkNotNegative(increase, "Increase must be non-negative!");
        if (increase == 0) {
            return;
        }
        long clusterWideThreadId = this.getOrCreateUniqueThreadId(this.groupId);
        ChangePermitsOp op = new ChangePermitsOp(this.objectName, -1L, clusterWideThreadId, UuidUtil.newUnsecureUUID(), increase);
        this.invocationManager.invoke(this.groupId, op).join();
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

