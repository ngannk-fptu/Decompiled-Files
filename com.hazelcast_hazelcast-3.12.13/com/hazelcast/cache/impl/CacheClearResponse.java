/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.io.Serializable;

public class CacheClearResponse
implements IdentifiedDataSerializable,
Serializable {
    private Object response;

    public CacheClearResponse() {
    }

    public CacheClearResponse(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return this.response;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 25;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.response);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.response = in.readObject();
    }
}

