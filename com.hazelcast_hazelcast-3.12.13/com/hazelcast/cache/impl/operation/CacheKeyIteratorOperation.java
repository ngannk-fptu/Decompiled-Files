/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class CacheKeyIteratorOperation
extends KeyBasedCacheOperation
implements ReadonlyOperation {
    private int tableIndex;
    private int size;

    public CacheKeyIteratorOperation() {
    }

    public CacheKeyIteratorOperation(String name, int tableIndex, int size) {
        super(name, new HeapData());
        this.tableIndex = tableIndex;
        this.size = size;
    }

    @Override
    public int getId() {
        return 22;
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.fetchKeys(this.tableIndex, this.size);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.tableIndex);
        out.writeInt(this.size);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.tableIndex = in.readInt();
        this.size = in.readInt();
    }
}

