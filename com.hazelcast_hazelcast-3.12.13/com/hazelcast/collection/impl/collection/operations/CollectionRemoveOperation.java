/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionRemoveBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CollectionRemoveOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    private Data value;
    private long itemId = -1L;

    public CollectionRemoveOperation() {
    }

    public CollectionRemoveOperation(String name, Data value) {
        super(name);
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        this.response = false;
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        CollectionItem item = collectionContainer.remove(this.value);
        if (item != null) {
            this.response = true;
            this.itemId = item.getItemId();
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (this.itemId != -1L) {
            this.publishEvent(ItemEventType.REMOVED, this.value);
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.itemId != -1L;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionRemoveBackupOperation(this.name, this.itemId);
    }

    @Override
    public int getId() {
        return 5;
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

