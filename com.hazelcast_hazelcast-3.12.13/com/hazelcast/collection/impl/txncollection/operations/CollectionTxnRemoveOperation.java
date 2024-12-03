/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.txncollection.CollectionTxnOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnRemoveBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CollectionTxnRemoveOperation
extends CollectionBackupAwareOperation
implements CollectionTxnOperation,
MutatingOperation {
    private long itemId;
    private transient CollectionItem item;

    public CollectionTxnRemoveOperation() {
    }

    public CollectionTxnRemoveOperation(String name, long itemId) {
        super(name);
        this.itemId = itemId;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionTxnRemoveBackupOperation(this.name, this.itemId);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.item = collectionContainer.commitRemove(this.itemId);
    }

    @Override
    public void afterRun() throws Exception {
        if (this.item != null) {
            this.publishEvent(ItemEventType.REMOVED, this.item.getValue());
        }
    }

    @Override
    public long getItemId() {
        return this.itemId;
    }

    @Override
    public boolean isRemoveOperation() {
        return true;
    }

    @Override
    public int getId() {
        return 28;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.itemId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemId = in.readLong();
    }
}

