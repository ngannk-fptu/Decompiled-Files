/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import java.io.IOException;

public class VersionResponsePair
implements IdentifiedDataSerializable {
    private Object response;
    private long version;

    public VersionResponsePair() {
    }

    public VersionResponsePair(Object response, long version) {
        this.response = response;
        this.version = version;
    }

    public Object getResponse() {
        return this.response;
    }

    public long getVersion() {
        return this.version;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        IOUtil.writeObject(out, this.response);
        out.writeLong(this.version);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.response = IOUtil.readObject(in);
        this.version = in.readLong();
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }
}

