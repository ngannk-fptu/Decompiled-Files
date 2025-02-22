/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.LoadAllOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapLoadAllOperationFactory
extends AbstractMapOperationFactory {
    private String name;
    private List<Data> keys;
    private boolean replaceExistingValues;

    public MapLoadAllOperationFactory() {
        this.keys = Collections.emptyList();
    }

    public MapLoadAllOperationFactory(String name, List<Data> keys, boolean replaceExistingValues) {
        this.name = name;
        this.keys = keys;
        this.replaceExistingValues = replaceExistingValues;
    }

    @Override
    public Operation createOperation() {
        return new LoadAllOperation(this.name, this.keys, this.replaceExistingValues);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        int size = this.keys.size();
        out.writeInt(size);
        for (Data key : this.keys) {
            out.writeData(key);
        }
        out.writeBoolean(this.replaceExistingValues);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        int size = in.readInt();
        if (size > 0) {
            this.keys = new ArrayList<Data>(size);
        }
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keys.add(data);
        }
        this.replaceExistingValues = in.readBoolean();
    }

    @Override
    public int getId() {
        return 84;
    }
}

