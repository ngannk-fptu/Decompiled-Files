/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.WaitNotifyKey;

public abstract class LockAwareOperation
extends KeyBasedMapOperation
implements BlockingOperation {
    protected LockAwareOperation() {
    }

    protected LockAwareOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    protected LockAwareOperation(String name, Data dataKey, long ttl, long maxIdle) {
        super(name, dataKey, ttl, maxIdle);
    }

    protected LockAwareOperation(String name, Data dataKey, Data dataValue, long ttl, long maxIdle) {
        super(name, dataKey, dataValue, ttl, maxIdle);
    }

    @Override
    public boolean shouldWait() {
        return !this.recordStore.canAcquireLock(this.dataKey, this.getCallerUuid(), this.getThreadId());
    }

    @Override
    public abstract void onWaitExpire();

    @Override
    public final WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(this.getServiceNamespace(), this.dataKey);
    }
}

