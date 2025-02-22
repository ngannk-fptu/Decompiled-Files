/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CollectionReserveAddOperation
extends CollectionOperation
implements MutatingOperation {
    private String transactionId;
    private Data value;

    public CollectionReserveAddOperation() {
    }

    public CollectionReserveAddOperation(String name, String transactionId, Data value) {
        super(name);
        this.transactionId = transactionId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.response = collectionContainer.reserveAdd(this.transactionId, this.value);
    }

    @Override
    public int getId() {
        return 24;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.transactionId);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.transactionId = in.readUTF();
        this.value = in.readData();
    }
}

