/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.operation.CacheGetAllOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetAllOperationFactory
implements OperationFactory,
IdentifiedDataSerializable {
    private String name;
    private Set<Data> keys;
    private ExpiryPolicy expiryPolicy;

    public CacheGetAllOperationFactory() {
        this.keys = new HashSet<Data>();
    }

    public CacheGetAllOperationFactory(String name, Set<Data> keys, ExpiryPolicy expiryPolicy) {
        this.name = name;
        this.keys = keys;
        this.expiryPolicy = expiryPolicy;
    }

    @Override
    public Operation createOperation() {
        return new CacheGetAllOperation(this.name, this.keys, this.expiryPolicy);
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.expiryPolicy);
        out.writeInt(this.keys.size());
        for (Data key : this.keys) {
            out.writeData(key);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
        int size = in.readInt();
        this.keys = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keys.add(data);
        }
    }
}

