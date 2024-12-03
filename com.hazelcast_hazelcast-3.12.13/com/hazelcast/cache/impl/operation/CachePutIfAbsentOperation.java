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

public class CachePutIfAbsentOperation
extends MutatingCacheOperation {
    private Data value;
    private ExpiryPolicy expiryPolicy;

    public CachePutIfAbsentOperation() {
    }

    public CachePutIfAbsentOperation(String cacheNameWithPrefix, Data key, Data value, ExpiryPolicy expiryPolicy, int completionId) {
        super(cacheNameWithPrefix, key, completionId);
        this.value = value;
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.putIfAbsent(this.key, this.value, this.expiryPolicy, this.getCallerUuid(), this.completionId);
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
        out.writeObject(this.expiryPolicy);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 4;
    }
}

