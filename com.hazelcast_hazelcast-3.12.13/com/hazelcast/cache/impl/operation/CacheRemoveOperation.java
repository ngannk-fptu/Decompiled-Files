/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheRemoveBackupOperation;
import com.hazelcast.cache.impl.operation.MutatingCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class CacheRemoveOperation
extends MutatingCacheOperation {
    private Data oldValue;

    public CacheRemoveOperation() {
    }

    public CacheRemoveOperation(String cacheNameWithPrefix, Data key, Data oldValue, int completionId) {
        super(cacheNameWithPrefix, key, completionId);
        this.oldValue = oldValue;
    }

    @Override
    public void run() throws Exception {
        this.response = this.oldValue == null ? Boolean.valueOf(this.recordStore.remove(this.key, this.getCallerUuid(), null, this.completionId)) : Boolean.valueOf(this.recordStore.remove(this.key, this.oldValue, this.getCallerUuid(), null, this.completionId));
    }

    @Override
    public void afterRun() throws Exception {
        if (Boolean.TRUE.equals(this.response)) {
            this.publishWanRemove(this.key);
        }
        super.afterRun();
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new CacheRemoveBackupOperation(this.name, this.key);
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.oldValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.oldValue = in.readData();
    }
}

