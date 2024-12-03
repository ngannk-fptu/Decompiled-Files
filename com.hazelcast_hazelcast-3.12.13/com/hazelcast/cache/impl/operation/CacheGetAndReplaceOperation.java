/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CachePutBackupOperation;
import com.hazelcast.cache.impl.operation.MutatingCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetAndReplaceOperation
extends MutatingCacheOperation {
    private Data value;
    private ExpiryPolicy expiryPolicy;

    public CacheGetAndReplaceOperation() {
    }

    public CacheGetAndReplaceOperation(String cacheNameWithPrefix, Data key, Data value, ExpiryPolicy expiryPolicy, int completionId) {
        super(cacheNameWithPrefix, key, completionId);
        this.value = value;
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.getAndReplace(this.key, this.value, this.expiryPolicy, this.getCallerUuid(), this.completionId);
        this.backupRecord = this.recordStore.getRecord(this.key);
    }

    @Override
    public void afterRun() throws Exception {
        this.publishWanUpdate(this.key, this.backupRecord);
        super.afterRun();
    }

    @Override
    public boolean shouldBackup() {
        return this.response != null && this.backupRecord != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutBackupOperation(this.name, this.key, this.backupRecord);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.value);
        out.writeObject(this.expiryPolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.value = in.readData();
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 8;
    }
}

