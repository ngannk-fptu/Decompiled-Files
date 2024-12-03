/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;

public class PutIfAbsentOperation
extends BasePutOperation
implements MutatingOperation {
    private boolean successful;

    public PutIfAbsentOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        super(name, dataKey, value, ttl, maxIdle);
    }

    public PutIfAbsentOperation() {
    }

    @Override
    public void run() {
        Object oldValue = this.recordStore.putIfAbsent(this.dataKey, this.dataValue, this.ttl, this.maxIdle, this.getCallerAddress());
        this.oldValue = this.mapServiceContext.toData(oldValue);
        this.successful = this.oldValue == null;
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
    public boolean shouldBackup() {
        return this.successful && this.recordStore.getRecord(this.dataKey) != null;
    }

    @Override
    public int getId() {
        return 56;
    }
}

