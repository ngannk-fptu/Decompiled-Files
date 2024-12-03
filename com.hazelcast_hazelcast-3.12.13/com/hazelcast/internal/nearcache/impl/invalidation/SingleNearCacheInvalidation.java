/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;
import java.util.UUID;

public class SingleNearCacheInvalidation
extends Invalidation {
    private Data key;

    public SingleNearCacheInvalidation() {
    }

    public SingleNearCacheInvalidation(Data key, String dataStructureName, String sourceUuid, UUID partitionUuid, long sequence) {
        super(dataStructureName, sourceUuid, partitionUuid, sequence);
        this.key = key;
    }

    @Override
    public final Data getKey() {
        return this.key;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.key);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.key = in.readData();
    }

    @Override
    public String toString() {
        return "SingleNearCacheInvalidation{key=" + this.key + ", " + super.toString() + '}';
    }

    @Override
    public int getId() {
        return 36;
    }
}

