/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.operation.CacheLoadAllOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class CacheLoadAllOperationFactory
implements OperationFactory,
IdentifiedDataSerializable {
    private String name;
    private Set<Data> keys;
    private boolean replaceExistingValues;

    public CacheLoadAllOperationFactory(String name, Set<Data> keys, boolean replaceExistingValues) {
        this.name = name;
        this.keys = keys;
        this.replaceExistingValues = replaceExistingValues;
    }

    public CacheLoadAllOperationFactory() {
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 20;
    }

    @Override
    public Operation createOperation() {
        return new CacheLoadAllOperation(this.name, this.keys, this.replaceExistingValues);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeBoolean(this.replaceExistingValues);
        out.writeBoolean(this.keys != null);
        if (this.keys != null) {
            out.writeInt(this.keys.size());
            for (Data key : this.keys) {
                out.writeData(key);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.replaceExistingValues = in.readBoolean();
        boolean isKeysNotNull = in.readBoolean();
        if (isKeysNotNull) {
            int size = in.readInt();
            this.keys = SetUtil.createHashSet(size);
            for (int i = 0; i < size; ++i) {
                Data key = in.readData();
                this.keys.add(key);
            }
        }
    }
}

