/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class ReplaceIfSameOperation
extends BasePutOperation
implements MutatingOperation {
    private Data expect;
    private boolean successful;

    public ReplaceIfSameOperation() {
    }

    public ReplaceIfSameOperation(String name, Data dataKey, Data expect, Data update) {
        super(name, dataKey, update);
        this.expect = expect;
    }

    @Override
    public void run() {
        this.successful = this.recordStore.replace(this.dataKey, this.expect, this.dataValue);
        if (this.successful) {
            this.oldValue = this.expect;
        }
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
        return this.successful && this.recordStore.getRecord(this.dataKey) != null;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.expect);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expect = in.readData();
    }

    @Override
    public int getId() {
        return 62;
    }
}

