/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.impl.record.AbstractCacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import javax.cache.expiry.ExpiryPolicy;

public class CacheObjectRecord
extends AbstractCacheRecord<Object, ExpiryPolicy> {
    protected Object value;
    protected ExpiryPolicy expiryPolicy;

    public CacheObjectRecord() {
    }

    public CacheObjectRecord(Object value, long creationTime, long expiryTime) {
        super(creationTime, expiryTime);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public void setExpiryPolicy(ExpiryPolicy expiryPolicy) {
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public ExpiryPolicy getExpiryPolicy() {
        return this.expiryPolicy;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.value);
        if (out.getVersion().isGreaterOrEqual(EXPIRY_POLICY_VERSION)) {
            out.writeObject(this.expiryPolicy);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.value = in.readObject();
        if (in.getVersion().isGreaterOrEqual(EXPIRY_POLICY_VERSION)) {
            this.expiryPolicy = (ExpiryPolicy)in.readObject();
        }
    }

    @Override
    public int getId() {
        return 48;
    }
}

