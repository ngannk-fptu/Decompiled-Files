/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.exception.WaitKeyCancelledException;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireResult;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphore;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreRegistry;
import com.hazelcast.cp.internal.datastructures.semaphore.ReleaseResult;
import com.hazelcast.cp.internal.datastructures.semaphore.SemaphoreEndpoint;
import com.hazelcast.cp.internal.datastructures.semaphore.proxy.RaftSessionAwareSemaphoreProxy;
import com.hazelcast.cp.internal.datastructures.semaphore.proxy.RaftSessionlessSemaphoreProxy;
import com.hazelcast.cp.internal.datastructures.spi.blocking.AbstractBlockingService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.UUID;

public class RaftSemaphoreService
extends AbstractBlockingService<AcquireInvocationKey, RaftSemaphore, RaftSemaphoreRegistry> {
    public static final String SERVICE_NAME = "hz:raft:semaphoreService";

    public RaftSemaphoreService(NodeEngine nodeEngine) {
        super(nodeEngine);
    }

    private CPSemaphoreConfig getConfig(String name) {
        return this.nodeEngine.getConfig().getCPSubsystemConfig().findSemaphoreConfig(name);
    }

    public boolean initSemaphore(CPGroupId groupId, String name, int permits) {
        try {
            Collection<AcquireInvocationKey> acquired = ((RaftSemaphoreRegistry)this.getOrInitRegistry(groupId)).init(name, permits);
            this.notifyWaitKeys(groupId, name, acquired, true);
            return true;
        }
        catch (IllegalStateException ignored) {
            return false;
        }
    }

    public int availablePermits(CPGroupId groupId, String name) {
        RaftSemaphoreRegistry registry = (RaftSemaphoreRegistry)this.getRegistryOrNull(groupId);
        return registry != null ? registry.availablePermits(name) : 0;
    }

    public AcquireResult acquirePermits(CPGroupId groupId, String name, AcquireInvocationKey key, long timeoutMs) {
        this.heartbeatSession(groupId, key.sessionId());
        AcquireResult result = ((RaftSemaphoreRegistry)this.getOrInitRegistry(groupId)).acquire(name, key, timeoutMs);
        if (this.logger.isFineEnabled()) {
            if (result.status() == AcquireResult.AcquireStatus.SUCCESSFUL) {
                this.logger.fine("Semaphore[" + name + "] in " + groupId + " acquired permits: " + key.permits() + " by <" + key.endpoint() + ", " + key.invocationUid() + "> at commit index: " + key.commitIndex());
            } else if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
                this.logger.fine("Semaphore[" + name + "] in " + groupId + " wait key added for permits: " + key.permits() + " by <" + key.endpoint() + ", " + key.invocationUid() + "> at commit index: " + key.commitIndex());
            } else if (result.status() == AcquireResult.AcquireStatus.FAILED) {
                this.logger.fine("Semaphore[" + name + "] in " + groupId + " not acquired permits: " + key.permits() + " by <" + key.endpoint() + ", " + key.invocationUid() + "> at commit index: " + key.commitIndex());
            }
        }
        this.notifyCancelledWaitKeys(groupId, name, result.cancelledWaitKeys());
        if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
            this.scheduleTimeout(groupId, name, key.invocationUid(), timeoutMs);
        }
        return result;
    }

    public void releasePermits(CPGroupId groupId, long commitIndex, String name, SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        this.heartbeatSession(groupId, endpoint.sessionId());
        ReleaseResult result = ((RaftSemaphoreRegistry)this.getOrInitRegistry(groupId)).release(name, endpoint, invocationUid, permits);
        this.notifyCancelledWaitKeys(groupId, name, result.cancelledWaitKeys());
        this.notifyWaitKeys(groupId, name, result.acquiredWaitKeys(), true);
        if (this.logger.isFineEnabled()) {
            if (result.success()) {
                this.logger.fine("Semaphore[" + name + "] in " + groupId + " released permits: " + permits + " by <" + endpoint + ", " + invocationUid + "> at commit index: " + commitIndex + " new acquires: " + result.acquiredWaitKeys());
            } else {
                this.logger.fine("Semaphore[" + name + "] in " + groupId + " not-released permits: " + permits + " by <" + endpoint + ", " + invocationUid + "> at commit index: " + commitIndex);
            }
        }
        if (!result.success()) {
            throw new IllegalArgumentException();
        }
    }

    public int drainPermits(CPGroupId groupId, String name, long commitIndex, SemaphoreEndpoint endpoint, UUID invocationUid) {
        this.heartbeatSession(groupId, endpoint.sessionId());
        AcquireResult result = ((RaftSemaphoreRegistry)this.getOrInitRegistry(groupId)).drainPermits(name, endpoint, invocationUid);
        this.notifyCancelledWaitKeys(groupId, name, result.cancelledWaitKeys());
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Semaphore[" + name + "] in " + groupId + " drained permits: " + result.permits() + " by <" + endpoint + ", " + invocationUid + "> at commit index: " + commitIndex);
        }
        return result.permits();
    }

    public boolean changePermits(CPGroupId groupId, long commitIndex, String name, SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        this.heartbeatSession(groupId, endpoint.sessionId());
        ReleaseResult result = ((RaftSemaphoreRegistry)this.getOrInitRegistry(groupId)).changePermits(name, endpoint, invocationUid, permits);
        this.notifyCancelledWaitKeys(groupId, name, result.cancelledWaitKeys());
        this.notifyWaitKeys(groupId, name, result.acquiredWaitKeys(), true);
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Semaphore[" + name + "] in " + groupId + " changed permits: " + permits + " by <" + endpoint + ", " + invocationUid + "> at commit index: " + commitIndex + ". new acquires: " + result.acquiredWaitKeys());
        }
        return result.success();
    }

    private void notifyCancelledWaitKeys(CPGroupId groupId, String name, Collection<AcquireInvocationKey> keys) {
        if (keys.isEmpty()) {
            return;
        }
        this.notifyWaitKeys(groupId, name, keys, new WaitKeyCancelledException());
    }

    @Override
    protected RaftSemaphoreRegistry createNewRegistry(CPGroupId groupId) {
        return new RaftSemaphoreRegistry(groupId);
    }

    @Override
    protected Object expiredWaitKeyResponse() {
        return false;
    }

    @Override
    protected String serviceName() {
        return SERVICE_NAME;
    }

    public ISemaphore createProxy(String proxyName) {
        try {
            proxyName = RaftService.withoutDefaultGroupName(proxyName);
            RaftGroupId groupId = this.raftService.createRaftGroupForProxy(proxyName);
            String objectName = RaftService.getObjectNameForProxy(proxyName);
            CPSemaphoreConfig config = this.getConfig(proxyName);
            return config != null && config.isJDKCompatible() ? new RaftSessionlessSemaphoreProxy(this.nodeEngine, groupId, proxyName, objectName) : new RaftSessionAwareSemaphoreProxy(this.nodeEngine, groupId, proxyName, objectName);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

