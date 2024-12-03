/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.map.impl.EntryViews;
import com.hazelcast.map.impl.operation.ReadonlyKeyBasedMapOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.WaitNotifyKey;

public class GetEntryViewOperation
extends ReadonlyKeyBasedMapOperation
implements BlockingOperation {
    private EntryView<Data, Data> result;

    public GetEntryViewOperation() {
    }

    public GetEntryViewOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    @Override
    public void run() {
        Object record = this.recordStore.getRecordOrNull(this.dataKey);
        if (record != null) {
            Data value = this.mapServiceContext.toData(record.getValue());
            this.result = EntryViews.createSimpleEntryView(this.dataKey, value, record);
        }
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(this.getServiceNamespace(), this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        return this.recordStore.isTransactionallyLocked(this.dataKey) && !this.recordStore.canAcquireLock(this.dataKey, this.getCallerUuid(), this.getThreadId());
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(new OperationTimeoutException("Cannot read transactionally locked entry!"));
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public int getId() {
        return 46;
    }
}

