/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetOperation
extends KeyBasedCacheOperation
implements ReadonlyOperation {
    private ExpiryPolicy expiryPolicy;

    public CacheGetOperation() {
    }

    public CacheGetOperation(String cacheNameWithPrefix, Data key, ExpiryPolicy expiryPolicy) {
        super(cacheNameWithPrefix, key);
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.get(this.key, this.expiryPolicy);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.expiryPolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 1;
    }
}

