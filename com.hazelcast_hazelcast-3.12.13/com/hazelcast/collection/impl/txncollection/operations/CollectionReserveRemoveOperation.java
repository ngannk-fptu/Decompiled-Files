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

public class CollectionReserveRemoveOperation
extends CollectionOperation
implements MutatingOperation {
    private String transactionId;
    private Data value;
    private long reservedItemId = -1L;

    public CollectionReserveRemoveOperation() {
    }

    public CollectionReserveRemoveOperation(String name, long reservedItemId, Data value, String transactionId) {
        super(name);
        this.reservedItemId = reservedItemId;
        this.value = value;
        this.transactionId = transactionId;
    }

    @Override
    public int getId() {
        return 25;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.response = collectionContainer.reserveRemove(this.reservedItemId, this.value, this.transactionId);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.reservedItemId);
        out.writeData(this.value);
        out.writeUTF(this.transactionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.reservedItemId = in.readLong();
        this.value = in.readData();
        this.transactionId = in.readUTF();
    }
}

