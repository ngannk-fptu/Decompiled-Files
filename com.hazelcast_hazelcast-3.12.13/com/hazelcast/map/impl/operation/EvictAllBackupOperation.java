/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.BackupOperation;

public class EvictAllBackupOperation
extends MapOperation
implements BackupOperation {
    public EvictAllBackupOperation() {
        this(null);
    }

    public EvictAllBackupOperation(String name) {
        super(name);
        this.createRecordStoreOnDemand = false;
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore == null) {
            return;
        }
        this.recordStore.evictAll(true);
    }

    @Override
    public int getId() {
        return 32;
    }
}

