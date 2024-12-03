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

public class CacheReplaceOperation
extends MutatingCacheOperation {
    private Data newValue;
    private Data oldValue;
    private ExpiryPolicy expiryPolicy;

    public CacheReplaceOperation() {
    }

    public CacheReplaceOperation(String cacheNameWithPrefix, Data key, Data oldValue, Data newValue, ExpiryPolicy expiryPolicy, int completionId) {
        super(cacheNameWithPrefix, key, completionId);
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public void run() throws Exception {
        this.response = this.oldValue == null ? Boolean.valueOf(this.recordStore.replace(this.key, this.newValue, this.expiryPolicy, this.getCallerUuid(), this.completionId)) : Boolean.valueOf(this.recordStore.replace(this.key, this.oldValue, this.newValue, this.expiryPolicy, this.getCallerUuid(), this.completionId));
        if (Boolean.TRUE.equals(this.response)) {
            this.backupRecord = this.recordStore.getRecord(this.key);
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (Boolean.TRUE.equals(this.response)) {
            this.publishWanUpdate(this.key, this.backupRecord);
        }
        super.afterRun();
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response) && this.backupRecord != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutBackupOperation(this.name, this.key, this.backupRecord);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.newValue);
        out.writeData(this.oldValue);
        out.writeObject(this.expiryPolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.newValue = in.readData();
        this.oldValue = in.readData();
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 7;
    }
}

