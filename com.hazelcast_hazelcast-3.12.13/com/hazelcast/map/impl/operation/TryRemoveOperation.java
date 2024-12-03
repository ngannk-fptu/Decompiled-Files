/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BaseRemoveOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public class TryRemoveOperation
extends BaseRemoveOperation {
    private boolean successful;

    public TryRemoveOperation() {
    }

    public TryRemoveOperation(String name, Data dataKey, long timeout) {
        super(name, dataKey);
        this.setWaitTimeout(timeout);
    }

    @Override
    public void run() {
        this.dataOldValue = this.mapServiceContext.toData(this.recordStore.remove(this.dataKey, this.getCallerProvenance()));
        this.successful = this.dataOldValue != null;
    }

    @Override
    public void afterRun() {
        if (this.successful) {
            super.afterRun();
        }
    }

    @Override
    public Object getResponse() {
        return this.successful;
    }

    @Override
    public boolean shouldBackup() {
        return this.successful;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
    }

    @Override
    public int getId() {
        return 64;
    }
}

