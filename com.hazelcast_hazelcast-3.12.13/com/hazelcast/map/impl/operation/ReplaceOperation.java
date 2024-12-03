/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;

public class ReplaceOperation
extends BasePutOperation
implements MutatingOperation {
    private boolean successful;

    public ReplaceOperation(String name, Data dataKey, Data value) {
        super(name, dataKey, value);
    }

    public ReplaceOperation() {
    }

    @Override
    public void run() {
        Object oldValue = this.recordStore.replace(this.dataKey, this.dataValue);
        this.oldValue = this.mapServiceContext.toData(oldValue);
        this.successful = oldValue != null;
    }

    @Override
    public boolean shouldBackup() {
        return this.successful && this.recordStore.getRecord(this.dataKey) != null;
    }

    @Override
    public void afterRun() {
        if (this.successful) {
            super.afterRun();
        }
    }

    @Override
    public Object getResponse() {
        return this.oldValue;
    }

    @Override
    public int getId() {
        return 24;
    }
}

