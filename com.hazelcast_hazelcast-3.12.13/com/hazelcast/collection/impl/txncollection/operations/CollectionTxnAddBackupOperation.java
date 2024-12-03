/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class CollectionTxnAddBackupOperation
extends CollectionOperation
implements BackupOperation {
    private long itemId;
    private Data value;

    public CollectionTxnAddBackupOperation() {
    }

    public CollectionTxnAddBackupOperation(String name, long itemId, Data value) {
        super(name);
        this.itemId = itemId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        collectionContainer.commitAddBackup(this.itemId, this.value);
        this.response = true;
    }

    @Override
    public int getId() {
        return 27;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.itemId);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemId = in.readLong();
        this.value = in.readData();
    }
}

