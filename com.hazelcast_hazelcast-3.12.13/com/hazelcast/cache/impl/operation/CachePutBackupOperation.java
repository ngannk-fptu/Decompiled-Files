/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class CachePutBackupOperation
extends KeyBasedCacheOperation
implements BackupOperation {
    private CacheRecord cacheRecord;
    private boolean wanOriginated;

    public CachePutBackupOperation() {
    }

    public CachePutBackupOperation(String name, Data key, CacheRecord cacheRecord) {
        this(name, key, cacheRecord, false);
    }

    public CachePutBackupOperation(String name, Data key, CacheRecord cacheRecord, boolean wanOriginated) {
        super(name, key);
        if (cacheRecord == null) {
            throw new IllegalArgumentException("Cache record of backup operation cannot be null!");
        }
        this.cacheRecord = cacheRecord;
        this.wanOriginated = wanOriginated;
    }

    @Override
    public void run() {
        if (this.recordStore != null) {
            this.recordStore.putRecord(this.key, this.cacheRecord, true);
            this.response = Boolean.TRUE;
        }
    }

    @Override
    public void afterRun() {
        if (this.recordStore != null && !this.wanOriginated) {
            this.publishWanUpdate(this.key, this.cacheRecord);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.cacheRecord);
        out.writeBoolean(this.wanOriginated);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.cacheRecord = (CacheRecord)in.readObject();
        this.wanOriginated = in.readBoolean();
    }

    @Override
    public int getId() {
        return 9;
    }
}

