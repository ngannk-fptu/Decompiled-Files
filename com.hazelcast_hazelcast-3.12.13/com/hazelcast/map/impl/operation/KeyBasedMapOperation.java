/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;

public abstract class KeyBasedMapOperation
extends MapOperation
implements PartitionAwareOperation,
Versioned {
    protected Data dataKey;
    protected long threadId;
    protected Data dataValue;
    protected long ttl = -1L;
    protected long maxIdle = -1L;

    public KeyBasedMapOperation() {
    }

    public KeyBasedMapOperation(String name, Data dataKey) {
        super(name);
        this.dataKey = dataKey;
    }

    protected KeyBasedMapOperation(String name, Data dataKey, Data dataValue) {
        super(name);
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    protected KeyBasedMapOperation(String name, Data dataKey, long ttl, long maxIdle) {
        super(name);
        this.dataKey = dataKey;
        this.ttl = ttl;
        this.maxIdle = maxIdle;
    }

    protected KeyBasedMapOperation(String name, Data dataKey, Data dataValue, long ttl, long maxIdle) {
        super(name);
        this.dataKey = dataKey;
        this.dataValue = dataValue;
        this.ttl = ttl;
        this.maxIdle = maxIdle;
    }

    public final Data getKey() {
        return this.dataKey;
    }

    @Override
    public final long getThreadId() {
        return this.threadId;
    }

    @Override
    public final void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public final Data getValue() {
        return this.dataValue;
    }

    public final long getTtl() {
        return this.ttl;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.dataKey);
        out.writeLong(this.threadId);
        out.writeData(this.dataValue);
        out.writeLong(this.ttl);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeLong(this.maxIdle);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.dataKey = in.readData();
        this.threadId = in.readLong();
        this.dataValue = in.readData();
        this.ttl = in.readLong();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.maxIdle = in.readLong();
        }
    }
}

