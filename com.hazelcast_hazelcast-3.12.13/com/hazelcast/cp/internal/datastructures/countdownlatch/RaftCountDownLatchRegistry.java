/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.countdownlatch.AwaitInvocationKey;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatch;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class RaftCountDownLatchRegistry
extends ResourceRegistry<AwaitInvocationKey, RaftCountDownLatch>
implements IdentifiedDataSerializable {
    RaftCountDownLatchRegistry() {
    }

    RaftCountDownLatchRegistry(CPGroupId groupId) {
        super(groupId);
    }

    @Override
    protected RaftCountDownLatch createNewResource(CPGroupId groupId, String name) {
        return new RaftCountDownLatch(groupId, name);
    }

    protected RaftCountDownLatchRegistry cloneForSnapshot() {
        RaftCountDownLatchRegistry clone = new RaftCountDownLatchRegistry();
        clone.groupId = this.groupId;
        for (Map.Entry e : this.resources.entrySet()) {
            clone.resources.put(e.getKey(), ((RaftCountDownLatch)e.getValue()).cloneForSnapshot());
        }
        clone.destroyedNames.addAll(this.destroyedNames);
        clone.waitTimeouts.putAll(this.waitTimeouts);
        return clone;
    }

    boolean trySetCount(String name, int count) {
        return ((RaftCountDownLatch)this.getOrInitResource(name)).trySetCount(count);
    }

    Tuple2<Integer, Collection<AwaitInvocationKey>> countDown(String name, UUID invocationUuid, int expectedRound) {
        RaftCountDownLatch latch = (RaftCountDownLatch)this.getOrInitResource(name);
        Tuple2<Integer, Collection<AwaitInvocationKey>> t = latch.countDown(invocationUuid, expectedRound);
        for (AwaitInvocationKey key : (Collection)t.element2) {
            this.removeWaitKey(name, key);
        }
        return t;
    }

    boolean await(String name, AwaitInvocationKey key, long timeoutMs) {
        boolean success = ((RaftCountDownLatch)this.getOrInitResource(name)).await(key, timeoutMs > 0L);
        if (!success) {
            this.addWaitKey(name, key, timeoutMs);
        }
        return success;
    }

    int getRemainingCount(String name) {
        return ((RaftCountDownLatch)this.getOrInitResource(name)).getRemainingCount();
    }

    int getRound(String name) {
        return ((RaftCountDownLatch)this.getOrInitResource(name)).getRound();
    }

    @Override
    public int getFactoryId() {
        return RaftCountDownLatchDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

