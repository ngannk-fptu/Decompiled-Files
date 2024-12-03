/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class CacheRemoveBackupOperation
extends KeyBasedCacheOperation
implements BackupOperation {
    private boolean wanOriginated;

    public CacheRemoveBackupOperation() {
    }

    public CacheRemoveBackupOperation(String name, Data key) {
        this(name, key, false);
    }

    public CacheRemoveBackupOperation(String name, Data key, boolean wanOriginated) {
        super(name, key, true);
        this.wanOriginated = wanOriginated;
    }

    @Override
    public void run() {
        if (this.recordStore != null) {
            this.recordStore.removeRecord(this.key);
        }
    }

    @Override
    public void afterRun() {
        if (this.recordStore != null && !this.wanOriginated) {
            this.publishWanRemove(this.key);
        }
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.wanOriginated);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.wanOriginated = in.readBoolean();
    }
}

