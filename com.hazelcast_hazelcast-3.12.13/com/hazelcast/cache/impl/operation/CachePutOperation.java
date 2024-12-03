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

public class CachePutOperation
extends MutatingCacheOperation {
    private Data value;
    private boolean get;
    private ExpiryPolicy expiryPolicy;

    public CachePutOperation() {
    }

    public CachePutOperation(String cacheNameWithPrefix, Data key, Data value, ExpiryPolicy expiryPolicy, boolean get, int completionId) {
        super(cacheNameWithPrefix, key, completionId);
        this.value = value;
        this.expiryPolicy = expiryPolicy;
        this.get = get;
    }

    @Override
    public void run() throws Exception {
        if (this.get) {
            this.response = this.recordStore.getAndPut(this.key, this.value, this.expiryPolicy, this.getCallerUuid(), this.completionId);
            this.backupRecord = this.recordStore.getRecord(this.key);
        } else {
            this.backupRecord = this.recordStore.put(this.key, this.value, this.expiryPolicy, this.getCallerUuid(), this.completionId);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.publishWanUpdate(this.key, this.backupRecord);
        super.afterRun();
    }

    @Override
    public boolean shouldBackup() {
        return this.backupRecord != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutBackupOperation(this.name, this.key, this.backupRecord);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.get);
        out.writeObject(this.expiryPolicy);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.get = in.readBoolean();
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 3;
    }
}

