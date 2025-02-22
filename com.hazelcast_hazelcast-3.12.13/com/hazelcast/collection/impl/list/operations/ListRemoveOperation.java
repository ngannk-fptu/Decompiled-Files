/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionRemoveBackupOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class ListRemoveOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    private int index;
    private long itemId;

    public ListRemoveOperation() {
    }

    public ListRemoveOperation(String name, int index) {
        super(name);
        this.index = index;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionRemoveBackupOperation(this.name, this.itemId);
    }

    @Override
    public void beforeRun() throws Exception {
        this.publishEvent(ItemEventType.REMOVED, (Data)this.response);
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        CollectionItem item = listContainer.remove(this.index);
        this.itemId = item.getItemId();
        this.response = item.getValue();
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.index);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.index = in.readInt();
    }
}

