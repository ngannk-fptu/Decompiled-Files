/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.serialization.Data;

public class TxnGenerateRecordIdOperation
extends AbstractKeyBasedMultiMapOperation {
    public TxnGenerateRecordIdOperation() {
    }

    public TxnGenerateRecordIdOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    @Override
    public void run() throws Exception {
        this.response = this.getOrCreateContainer().nextId();
    }

    @Override
    public int getId() {
        return 29;
    }
}

