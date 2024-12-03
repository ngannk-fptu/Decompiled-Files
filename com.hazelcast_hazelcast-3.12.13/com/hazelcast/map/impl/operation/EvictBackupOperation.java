/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class EvictBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    protected boolean unlockKey;

    public EvictBackupOperation() {
    }

    public EvictBackupOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    @Override
    public void run() {
        this.recordStore.evict(this.dataKey, true);
        if (this.unlockKey) {
            this.recordStore.forceUnlock(this.dataKey);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.publishWanRemove(this.dataKey);
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.unlockKey);
        out.writeBoolean(this.disableWanReplicationEvent);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.unlockKey = in.readBoolean();
        this.disableWanReplicationEvent = in.readBoolean();
    }
}

