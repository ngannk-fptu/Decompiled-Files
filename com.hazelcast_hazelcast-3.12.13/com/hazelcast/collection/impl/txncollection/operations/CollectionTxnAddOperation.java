/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.txncollection.CollectionTxnOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnAddBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CollectionTxnAddOperation
extends CollectionBackupAwareOperation
implements CollectionTxnOperation,
MutatingOperation {
    private long itemId;
    private Data value;

    public CollectionTxnAddOperation() {
    }

    public CollectionTxnAddOperation(String name, long itemId, Data value) {
        super(name);
        this.itemId = itemId;
        this.value = value;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionTxnAddBackupOperation(this.name, this.itemId, this.value);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        collectionContainer.commitAdd(this.itemId, this.value);
        this.response = true;
    }

    @Override
    public void afterRun() throws Exception {
        this.publishEvent(ItemEventType.ADDED, this.value);
    }

    @Override
    public long getItemId() {
        return this.itemId;
    }

    @Override
    public boolean isRemoveOperation() {
        return false;
    }

    @Override
    public int getId() {
        return 26;
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

