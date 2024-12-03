/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.exception.WaitKeyCancelledException;
import com.hazelcast.cp.internal.datastructures.lock.AcquireResult;
import com.hazelcast.cp.internal.datastructures.lock.LockEndpoint;
import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import com.hazelcast.cp.internal.datastructures.lock.RaftLock;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockRegistry;
import com.hazelcast.cp.internal.datastructures.lock.ReleaseResult;
import com.hazelcast.cp.internal.datastructures.lock.proxy.RaftFencedLockProxy;
import com.hazelcast.cp.internal.datastructures.spi.blocking.AbstractBlockingService;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RaftLockService
extends AbstractBlockingService<LockInvocationKey, RaftLock, RaftLockRegistry> {
    public static final String SERVICE_NAME = "hz:raft:lockService";
    private final ConcurrentMap<String, RaftFencedLockProxy> proxies = new ConcurrentHashMap<String, RaftFencedLockProxy>();

    public RaftLockService(NodeEngine nodeEngine) {
        super(nodeEngine);
    }

    @Override
    protected void initImpl() {
        super.initImpl();
    }

    public AcquireResult acquire(CPGroupId groupId, String name, LockInvocationKey key, long timeoutMs) {
        this.heartbeatSession(groupId, key.sessionId());
        RaftLockRegistry registry = (RaftLockRegistry)this.getOrInitRegistry(groupId);
        AcquireResult result = registry.acquire(name, key, timeoutMs);
        if (this.logger.isFineEnabled()) {
            if (result.status() == AcquireResult.AcquireStatus.SUCCESSFUL) {
                this.logger.fine("Lock[" + name + "] in " + groupId + " acquired by <" + key.endpoint() + ", " + key.invocationUid() + "> at commit index: " + key.commitIndex() + ". new lock state: " + registry.getLockOwnershipState(name));
            } else if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
                this.logger.fine("Lock[" + name + "] in " + groupId + " wait key added for <" + key.endpoint() + ", " + key.invocationUid() + "> at commit index: " + key.commitIndex() + ". lock state: " + registry.getLockOwnershipState(name));
            } else if (result.status() == AcquireResult.AcquireStatus.FAILED) {
                this.logger.fine("Lock[" + name + "] in " + groupId + " acquire failed for <" + key.endpoint() + ", " + key.invocationUid() + "> at commit index: " + key.commitIndex() + ". lock state: " + registry.getLockOwnershipState(name));
            }
        }
        if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
            this.scheduleTimeout(groupId, name, key.invocationUid(), timeoutMs);
        }
        this.notifyCancelledWaitKeys(groupId, name, result.cancelledWaitKeys());
        return result;
    }

    public boolean release(CPGroupId groupId, long commitIndex, String name, LockEndpoint endpoint, UUID invocationUid) {
        this.heartbeatSession(groupId, endpoint.sessionId());
        RaftLockRegistry registry = this.getLockRegistryOrFail(groupId, name);
        ReleaseResult result = registry.release(name, endpoint, invocationUid);
        if (this.logger.isFineEnabled()) {
            if (result.success()) {
                this.logger.fine("Lock[" + name + "] in " + groupId + " released by <" + endpoint + ", " + invocationUid + "> at commit index: " + commitIndex + ". new lock state: " + result.ownership());
            } else {
                this.logger.fine("Lock[" + name + "] in " + groupId + " not released by <" + endpoint + ", " + invocationUid + "> at commit index: " + commitIndex + ". lock state: " + registry.getLockOwnershipState(name));
            }
        }
        if (result.success()) {
            this.notifyWaitKeys(groupId, name, result.completedWaitKeys(), result.ownership().getFence());
            return result.ownership().isLockedBy(endpoint.sessionId(), endpoint.threadId());
        }
        this.notifyCancelledWaitKeys(groupId, name, result.completedWaitKeys());
        throw new IllegalMonitorStateException("Current thread is not owner of the lock!");
    }

    private void notifyCancelledWaitKeys(CPGroupId groupId, String name, Collection<LockInvocationKey> keys) {
        if (keys.isEmpty()) {
            return;
        }
        this.notifyWaitKeys(groupId, name, keys, new WaitKeyCancelledException());
    }

    public RaftLockOwnershipState getLockOwnershipState(CPGroupId groupId, String name) {
        Preconditions.checkNotNull(groupId);
        Preconditions.checkNotNull(name);
        RaftLockRegistry registry = (RaftLockRegistry)this.getRegistryOrNull(groupId);
        return registry != null ? registry.getLockOwnershipState(name) : RaftLockOwnershipState.NOT_LOCKED;
    }

    private RaftLockRegistry getLockRegistryOrFail(CPGroupId groupId, String name) {
        Preconditions.checkNotNull(groupId);
        RaftLockRegistry registry = (RaftLockRegistry)this.getRegistryOrNull(groupId);
        if (registry == null) {
            throw new IllegalMonitorStateException("Lock registry of " + groupId + " not found for Lock[" + name + "]");
        }
        return registry;
    }

    @Override
    protected RaftLockRegistry createNewRegistry(CPGroupId groupId) {
        return new RaftLockRegistry(this.nodeEngine.getConfig().getCPSubsystemConfig(), groupId);
    }

    @Override
    protected Object expiredWaitKeyResponse() {
        return 0L;
    }

    @Override
    protected void onRegistryRestored(RaftLockRegistry registry) {
        registry.setCpSubsystemConfig(this.nodeEngine.getConfig().getCPSubsystemConfig());
    }

    @Override
    protected String serviceName() {
        return SERVICE_NAME;
    }

    public FencedLock createProxy(String proxyName) {
        RaftFencedLockProxy proxy;
        RaftFencedLockProxy existing;
        proxyName = RaftService.withoutDefaultGroupName(proxyName);
        do {
            if ((proxy = (RaftFencedLockProxy)this.proxies.get(proxyName)) == null) continue;
            RaftGroupId groupId = this.raftService.createRaftGroupForProxy(proxyName);
            if (!((RaftGroupId)proxy.getGroupId()).equals(groupId)) {
                this.proxies.remove(proxyName, proxy);
                continue;
            }
            return proxy;
        } while ((existing = this.proxies.putIfAbsent(proxyName, proxy = this.doCreateProxy(proxyName))) != null);
        return proxy;
    }

    @Override
    public void onCPSubsystemRestart() {
        super.onCPSubsystemRestart();
        this.proxies.clear();
    }

    private RaftFencedLockProxy doCreateProxy(String proxyName) {
        try {
            RaftGroupId groupId = this.raftService.createRaftGroupForProxy(proxyName);
            return new RaftFencedLockProxy(this.nodeEngine, groupId, proxyName, RaftService.getObjectNameForProxy(proxyName));
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

