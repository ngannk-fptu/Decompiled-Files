/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BaseRemoveOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public class RemoveIfSameOperation
extends BaseRemoveOperation {
    private Data testValue;
    private boolean successful;

    public RemoveIfSameOperation() {
    }

    public RemoveIfSameOperation(String name, Data dataKey, Data value) {
        super(name, dataKey);
        this.testValue = value;
    }

    @Override
    public void run() {
        this.successful = this.recordStore.remove(this.dataKey, this.testValue);
    }

    @Override
    public void afterRun() {
        if (this.successful) {
            this.dataOldValue = this.testValue;
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
        this.sendResponse(null);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.testValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.testValue = in.readData();
    }

    @Override
    public int getId() {
        return 23;
    }
}

