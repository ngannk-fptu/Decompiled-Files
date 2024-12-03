/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.impl.record.AbstractCacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public class CacheDataRecord
extends AbstractCacheRecord<Data, Data> {
    private Data value;
    private Data expiryPolicy;

    public CacheDataRecord() {
    }

    public CacheDataRecord(Data value, long creationTime, long expiryTime) {
        super(creationTime, expiryTime);
        this.value = value;
    }

    @Override
    public Data getValue() {
        return this.value;
    }

    @Override
    public void setValue(Data value) {
        this.value = value;
    }

    @Override
    public void setExpiryPolicy(Data expiryPolicy) {
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public Data getExpiryPolicy() {
        return this.expiryPolicy;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.value);
        if (out.getVersion().isGreaterOrEqual(EXPIRY_POLICY_VERSION)) {
            out.writeData(this.expiryPolicy);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.value = in.readData();
        if (in.getVersion().isGreaterOrEqual(EXPIRY_POLICY_VERSION)) {
            this.expiryPolicy = in.readData();
        }
    }

    @Override
    public int getId() {
        return 47;
    }
}

