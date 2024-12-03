/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.lock.AcquireResult;
import com.hazelcast.cp.internal.datastructures.lock.LockEndpoint;
import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import com.hazelcast.cp.internal.datastructures.lock.RaftLock;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.datastructures.lock.ReleaseResult;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Map;
import java.util.UUID;

class RaftLockRegistry
extends ResourceRegistry<LockInvocationKey, RaftLock>
implements IdentifiedDataSerializable {
    private CPSubsystemConfig cpSubsystemConfig;

    RaftLockRegistry() {
    }

    RaftLockRegistry(CPSubsystemConfig cpSubsystemConfig, CPGroupId groupId) {
        super(groupId);
        this.cpSubsystemConfig = cpSubsystemConfig;
    }

    public void setCpSubsystemConfig(CPSubsystemConfig cpSubsystemConfig) {
        this.cpSubsystemConfig = cpSubsystemConfig;
    }

    @Override
    protected RaftLock createNewResource(CPGroupId groupId, String name) {
        FencedLockConfig lockConfig = this.cpSubsystemConfig.findLockConfig(name);
        int lockCountLimit = lockConfig != null ? lockConfig.getLockAcquireLimit() : 0;
        return new RaftLock(groupId, name, lockCountLimit);
    }

    protected RaftLockRegistry cloneForSnapshot() {
        RaftLockRegistry clone = new RaftLockRegistry();
        clone.groupId = this.groupId;
        for (Map.Entry e : this.resources.entrySet()) {
            clone.resources.put(e.getKey(), ((RaftLock)e.getValue()).cloneForSnapshot());
        }
        clone.destroyedNames.addAll(this.destroyedNames);
        clone.waitTimeouts.putAll(this.waitTimeouts);
        return clone;
    }

    AcquireResult acquire(String name, LockInvocationKey key, long timeoutMs) {
        AcquireResult result = ((RaftLock)this.getOrInitResource(name)).acquire(key, timeoutMs != 0L);
        for (LockInvocationKey cancelled : result.cancelledWaitKeys()) {
            this.removeWaitKey(name, cancelled);
        }
        if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
            this.addWaitKey(name, key, timeoutMs);
        }
        return result;
    }

    ReleaseResult release(String name, LockEndpoint endpoint, UUID invocationUid) {
        RaftLock lock = (RaftLock)this.getResourceOrNull(name);
        if (lock == null) {
            return ReleaseResult.FAILED;
        }
        ReleaseResult result = lock.release(endpoint, invocationUid);
        for (LockInvocationKey key : result.completedWaitKeys()) {
            this.removeWaitKey(name, key);
        }
        return result;
    }

    RaftLockOwnershipState getLockOwnershipState(String name) {
        RaftLock lock = (RaftLock)this.getResourceOrNull(name);
        return lock != null ? lock.lockOwnershipState() : RaftLockOwnershipState.NOT_LOCKED;
    }

    @Override
    public int getFactoryId() {
        return RaftLockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

