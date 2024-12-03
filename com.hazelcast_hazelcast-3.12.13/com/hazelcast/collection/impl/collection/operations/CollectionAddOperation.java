/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionAddBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CollectionAddOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    protected Data value;
    protected long itemId = -1L;

    public CollectionAddOperation() {
    }

    public CollectionAddOperation(String name, Data value) {
        super(name);
        this.value = value;
    }

    @Override
    public boolean shouldBackup() {
        return this.itemId != -1L;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionAddBackupOperation(this.name, this.itemId, this.value);
    }

    @Override
    public void run() throws Exception {
        if (this.hasEnoughCapacity(1)) {
            CollectionContainer collectionContainer = this.getOrCreateContainer();
            this.itemId = collectionContainer.add(this.value);
        }
        this.response = this.itemId != -1L;
    }

    @Override
    public void afterRun() throws Exception {
        if (this.itemId != -1L) {
            this.publishEvent(ItemEventType.ADDED, this.value);
        }
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.value = in.readData();
    }
}

