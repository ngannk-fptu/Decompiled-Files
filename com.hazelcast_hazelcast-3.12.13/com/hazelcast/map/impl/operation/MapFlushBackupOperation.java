/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class MapFlushBackupOperation
extends MapOperation
implements BackupOperation {
    public MapFlushBackupOperation() {
    }

    public MapFlushBackupOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        this.recordStore.softFlush();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
    }

    @Override
    public int getId() {
        return 49;
    }
}

