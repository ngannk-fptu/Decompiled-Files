/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheRemoveBackupOperation;
import com.hazelcast.cache.impl.operation.MutatingCacheOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;

public class CacheGetAndRemoveOperation
extends MutatingCacheOperation {
    public CacheGetAndRemoveOperation() {
    }

    public CacheGetAndRemoveOperation(String name, Data key, int completionId) {
        super(name, key, completionId);
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.getAndRemove(this.key, this.getCallerUuid(), this.completionId);
    }

    @Override
    public void afterRun() throws Exception {
        if (this.response != null) {
            this.publishWanRemove(this.key);
        }
        super.afterRun();
    }

    @Override
    public boolean shouldBackup() {
        return this.response != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new CacheRemoveBackupOperation(this.name, this.key);
    }

    @Override
    public int getId() {
        return 6;
    }
}

