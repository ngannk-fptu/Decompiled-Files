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

public class RemoveBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    protected boolean unlockKey;

    public RemoveBackupOperation() {
    }

    public RemoveBackupOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    public RemoveBackupOperation(String name, Data dataKey, boolean unlockKey) {
        super(name, dataKey);
        this.unlockKey = unlockKey;
    }

    public RemoveBackupOperation(String name, Data dataKey, boolean unlockKey, boolean disableWanReplicationEvent) {
        super(name, dataKey);
        this.unlockKey = unlockKey;
        this.disableWanReplicationEvent = disableWanReplicationEvent;
    }

    @Override
    public void run() {
        this.recordStore.removeBackup(this.dataKey, this.getCallerProvenance());
        if (this.unlockKey) {
            this.recordStore.forceUnlock(this.dataKey);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.publishWanRemove(this.dataKey);
        this.evict(this.dataKey);
        super.afterRun();
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public int getId() {
        return 4;
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

