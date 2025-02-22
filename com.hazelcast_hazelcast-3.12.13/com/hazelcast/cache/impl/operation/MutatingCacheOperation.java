/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.cache.impl.operation.MutableOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public abstract class MutatingCacheOperation
extends KeyBasedCacheOperation
implements BackupAwareOperation,
MutableOperation,
MutatingOperation {
    protected int completionId;

    protected MutatingCacheOperation() {
    }

    protected MutatingCacheOperation(String name, Data key, int completionId) {
        super(name, key);
        this.completionId = completionId;
    }

    @Override
    public int getCompletionId() {
        return this.completionId;
    }

    @Override
    public void setCompletionId(int completionId) {
        this.completionId = completionId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.completionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.completionId = in.readInt();
    }
}

