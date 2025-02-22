/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.operation.CacheRemoveAllOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class CacheRemoveAllOperationFactory
implements OperationFactory,
IdentifiedDataSerializable {
    private String name;
    private Set<Data> keys;
    private int completionId;

    public CacheRemoveAllOperationFactory() {
    }

    public CacheRemoveAllOperationFactory(String name, Set<Data> keys, int completionId) {
        this.name = name;
        this.keys = keys;
        this.completionId = completionId;
    }

    @Override
    public Operation createOperation() {
        return new CacheRemoveAllOperation(this.name, this.keys, this.completionId);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.completionId);
        out.writeInt(this.keys == null ? -1 : this.keys.size());
        if (this.keys != null) {
            for (Data key : this.keys) {
                out.writeData(key);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.completionId = in.readInt();
        int size = in.readInt();
        if (size == -1) {
            return;
        }
        this.keys = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            this.keys.add(in.readData());
        }
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 36;
    }
}

