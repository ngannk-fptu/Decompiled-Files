/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;

public class SetTtlBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    public SetTtlBackupOperation() {
    }

    public SetTtlBackupOperation(String name, Data dataKey, long ttl) {
        super(name, dataKey, ttl, -1L);
    }

    @Override
    public int getId() {
        return 149;
    }

    @Override
    public void run() throws Exception {
        this.recordStore.setTtl(this.dataKey, this.ttl);
    }

    @Override
    public void afterRun() throws Exception {
        Object record = this.recordStore.getRecord(this.dataKey);
        if (record != null) {
            this.publishWanUpdate(this.dataKey, record.getValue());
        }
    }
}

