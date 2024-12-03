/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;

public class PutOperation
extends BasePutOperation
implements MutatingOperation {
    public PutOperation() {
    }

    public PutOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        super(name, dataKey, value, ttl, maxIdle);
    }

    @Override
    public void run() {
        this.oldValue = this.mapServiceContext.toData(this.recordStore.put(this.dataKey, this.dataValue, this.ttl, this.maxIdle));
    }

    @Override
    public Object getResponse() {
        return this.oldValue;
    }

    @Override
    public int getId() {
        return 0;
    }
}

