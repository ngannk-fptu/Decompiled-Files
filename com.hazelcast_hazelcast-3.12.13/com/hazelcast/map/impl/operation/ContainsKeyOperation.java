/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.map.impl.operation.ReadonlyKeyBasedMapOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.WaitNotifyKey;

public class ContainsKeyOperation
extends ReadonlyKeyBasedMapOperation
implements BlockingOperation {
    private transient boolean containsKey;

    public ContainsKeyOperation() {
    }

    public ContainsKeyOperation(String name, Data dataKey) {
        this.name = name;
        this.dataKey = dataKey;
    }

    @Override
    public void run() {
        this.containsKey = this.recordStore.containsKey(this.dataKey, this.getCallerAddress());
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    public Object getResponse() {
        return this.containsKey;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(this.getServiceNamespace(), this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        if (this.recordStore.isTransactionallyLocked(this.dataKey)) {
            return !this.recordStore.canAcquireLock(this.dataKey, this.getCallerUuid(), this.getThreadId());
        }
        return false;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(new OperationTimeoutException("Cannot read transactionally locked entry!"));
    }
}

