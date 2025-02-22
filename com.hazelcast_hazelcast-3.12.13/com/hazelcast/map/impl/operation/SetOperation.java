/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;

public class SetOperation
extends BasePutOperation
implements MutatingOperation {
    private boolean newRecord;

    public SetOperation() {
    }

    public SetOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        super(name, dataKey, value, ttl, maxIdle);
    }

    @Override
    public void run() {
        this.oldValue = this.recordStore.set(this.dataKey, this.dataValue, this.ttl, this.maxIdle);
        this.newRecord = this.oldValue == null;
    }

    @Override
    public void afterRun() {
        this.eventType = this.newRecord ? EntryEventType.ADDED : EntryEventType.UPDATED;
        super.afterRun();
    }

    @Override
    public int getId() {
        return 15;
    }
}

