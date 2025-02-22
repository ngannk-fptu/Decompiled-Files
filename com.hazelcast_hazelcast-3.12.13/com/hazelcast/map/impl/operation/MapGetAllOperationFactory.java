/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.GetAllOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapGetAllOperationFactory
extends AbstractMapOperationFactory {
    private String name;
    private List<Data> keys = new ArrayList<Data>();

    public MapGetAllOperationFactory() {
    }

    public MapGetAllOperationFactory(String name, List<Data> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public Operation createOperation() {
        return new GetAllOperation(this.name, this.keys);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.keys.size());
        for (Data key : this.keys) {
            out.writeData(key);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keys.add(data);
        }
    }

    @Override
    public int getId() {
        return 83;
    }
}

