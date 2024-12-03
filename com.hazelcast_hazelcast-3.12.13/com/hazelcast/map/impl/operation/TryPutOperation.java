/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;

public class TryPutOperation
extends BasePutOperation
implements MutatingOperation {
    public TryPutOperation() {
    }

    public TryPutOperation(String name, Data dataKey, Data value, long timeout) {
        super(name, dataKey, value);
        this.setWaitTimeout(timeout);
    }

    @Override
    public void run() {
        this.recordStore.put(this.dataKey, this.dataValue, this.ttl, this.maxIdle);
    }

    @Override
    public boolean shouldBackup() {
        return this.recordStore.getRecord(this.dataKey) != null;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public int getId() {
        return 63;
    }
}

