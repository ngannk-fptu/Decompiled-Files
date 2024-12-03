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

public final class GetOperation
extends ReadonlyKeyBasedMapOperation
implements BlockingOperation {
    private Data result;

    public GetOperation() {
    }

    public GetOperation(String name, Data dataKey) {
        super(name, dataKey);
        this.dataKey = dataKey;
    }

    @Override
    public void run() {
        this.result = this.mapServiceContext.toData(this.recordStore.get(this.dataKey, false, this.getCallerAddress()));
    }

    @Override
    public void afterRun() {
        this.mapServiceContext.interceptAfterGet(this.name, this.result);
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

    @Override
    public Data getResponse() {
        return this.result;
    }

    @Override
    public int getId() {
        return 1;
    }
}

