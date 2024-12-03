/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.BackupOperation;

public class ClearBackupOperation
extends MapOperation
implements BackupOperation {
    public ClearBackupOperation() {
        this(null);
    }

    public ClearBackupOperation(String name) {
        super(name);
        this.createRecordStoreOnDemand = false;
    }

    @Override
    public void run() {
        if (this.recordStore != null) {
            this.recordStore.clear();
        }
    }

    @Override
    public int getId() {
        return 26;
    }
}

