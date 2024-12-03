/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public abstract class KeyBasedCacheOperation
extends CacheOperation {
    protected Data key;
    protected Object response;
    protected transient CacheRecord backupRecord;

    protected KeyBasedCacheOperation() {
    }

    protected KeyBasedCacheOperation(String name, Data key) {
        this(name, key, false);
    }

    protected KeyBasedCacheOperation(String name, Data key, boolean dontCreateCacheRecordStoreIfNotExist) {
        super(name, dontCreateCacheRecordStoreIfNotExist);
        this.key = key;
    }

    @Override
    public final Object getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.key);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.key = in.readData();
    }
}

