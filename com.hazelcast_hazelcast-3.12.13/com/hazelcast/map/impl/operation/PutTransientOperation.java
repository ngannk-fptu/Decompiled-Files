/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;

public class PutTransientOperation
extends BasePutOperation
implements MutatingOperation {
    public PutTransientOperation() {
    }

    public PutTransientOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        super(name, dataKey, value, ttl, maxIdle);
    }

    @Override
    public void run() {
        this.oldValue = this.mapServiceContext.toData(this.recordStore.putTransient(this.dataKey, this.dataValue, this.ttl, this.maxIdle));
        this.putTransient = true;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    public int getId() {
        return 61;
    }
}

