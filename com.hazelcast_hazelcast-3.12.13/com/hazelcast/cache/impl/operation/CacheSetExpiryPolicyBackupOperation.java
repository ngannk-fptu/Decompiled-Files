/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.operation.CacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheSetExpiryPolicyBackupOperation
extends CacheOperation
implements BackupOperation {
    private List<Data> keys;
    private Data expiryPolicy;

    public CacheSetExpiryPolicyBackupOperation() {
    }

    public CacheSetExpiryPolicyBackupOperation(String name, List<Data> keys, Data expiryPolicy) {
        super(name);
        this.keys = keys;
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore == null) {
            return;
        }
        this.recordStore.setExpiryPolicy(this.keys, this.expiryPolicy, null);
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        if (this.recordStore.isWanReplicationEnabled()) {
            for (Data key : this.keys) {
                CacheRecord record = this.recordStore.getRecord(key);
                this.publishWanUpdate(key, record);
            }
        }
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 68;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.keys.size());
        for (Data key : this.keys) {
            out.writeData(key);
        }
        out.writeData(this.expiryPolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int s = in.readInt();
        this.keys = new ArrayList<Data>(s);
        while (s-- > 0) {
            this.keys.add(in.readData());
        }
        this.expiryPolicy = in.readData();
    }
}

