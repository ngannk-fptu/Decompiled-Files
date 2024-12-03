/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireResult;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphore;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.semaphore.ReleaseResult;
import com.hazelcast.cp.internal.datastructures.semaphore.SemaphoreEndpoint;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class RaftSemaphoreRegistry
extends ResourceRegistry<AcquireInvocationKey, RaftSemaphore>
implements IdentifiedDataSerializable {
    RaftSemaphoreRegistry() {
    }

    RaftSemaphoreRegistry(CPGroupId groupId) {
        super(groupId);
    }

    @Override
    protected RaftSemaphore createNewResource(CPGroupId groupId, String name) {
        return new RaftSemaphore(groupId, name);
    }

    protected RaftSemaphoreRegistry cloneForSnapshot() {
        RaftSemaphoreRegistry clone = new RaftSemaphoreRegistry();
        clone.groupId = this.groupId;
        for (Map.Entry e : this.resources.entrySet()) {
            clone.resources.put(e.getKey(), ((RaftSemaphore)e.getValue()).cloneForSnapshot());
        }
        clone.destroyedNames.addAll(this.destroyedNames);
        clone.waitTimeouts.putAll(this.waitTimeouts);
        return clone;
    }

    Collection<AcquireInvocationKey> init(String name, int permits) {
        Collection<AcquireInvocationKey> acquired = ((RaftSemaphore)this.getOrInitResource(name)).init(permits);
        for (AcquireInvocationKey key : acquired) {
            this.removeWaitKey(name, key);
        }
        return acquired;
    }

    int availablePermits(String name) {
        RaftSemaphore semaphore = (RaftSemaphore)this.getResourceOrNull(name);
        return semaphore != null ? semaphore.getAvailable() : 0;
    }

    AcquireResult acquire(String name, AcquireInvocationKey key, long timeoutMs) {
        AcquireResult result = ((RaftSemaphore)this.getOrInitResource(name)).acquire(key, timeoutMs != 0L);
        for (AcquireInvocationKey waitKey : result.cancelledWaitKeys()) {
            this.removeWaitKey(name, waitKey);
        }
        if (result.status() == AcquireResult.AcquireStatus.WAIT_KEY_ADDED) {
            this.addWaitKey(name, key, timeoutMs);
        }
        return result;
    }

    ReleaseResult release(String name, SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        ReleaseResult result = ((RaftSemaphore)this.getOrInitResource(name)).release(endpoint, invocationUid, permits);
        for (AcquireInvocationKey key : result.acquiredWaitKeys()) {
            this.removeWaitKey(name, key);
        }
        for (AcquireInvocationKey key : result.cancelledWaitKeys()) {
            this.removeWaitKey(name, key);
        }
        return result;
    }

    AcquireResult drainPermits(String name, SemaphoreEndpoint endpoint, UUID invocationUid) {
        AcquireResult result = ((RaftSemaphore)this.getOrInitResource(name)).drain(endpoint, invocationUid);
        for (AcquireInvocationKey key : result.cancelledWaitKeys()) {
            this.removeWaitKey(name, key);
        }
        return result;
    }

    ReleaseResult changePermits(String name, SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        ReleaseResult result = ((RaftSemaphore)this.getOrInitResource(name)).change(endpoint, invocationUid, permits);
        for (AcquireInvocationKey key : result.acquiredWaitKeys()) {
            this.removeWaitKey(name, key);
        }
        for (AcquireInvocationKey key : result.cancelledWaitKeys()) {
            this.removeWaitKey(name, key);
        }
        return result;
    }

    @Override
    public int getFactoryId() {
        return RaftSemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

