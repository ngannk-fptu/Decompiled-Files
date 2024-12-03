/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.collection.impl.list.operations.ListSetBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class ListSetOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    private int index;
    private Data value;
    private long itemId = -1L;
    private long oldItemId = -1L;

    public ListSetOperation() {
    }

    public ListSetOperation(String name, int index, Data value) {
        super(name);
        this.index = index;
        this.value = value;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new ListSetBackupOperation(this.name, this.oldItemId, this.itemId, this.value);
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        this.itemId = listContainer.nextId();
        CollectionItem item = listContainer.set(this.index, this.itemId, this.value);
        this.oldItemId = item.getItemId();
        this.response = item.getValue();
    }

    @Override
    public void afterRun() throws Exception {
        this.publishEvent(ItemEventType.REMOVED, (Data)this.response);
        this.publishEvent(ItemEventType.ADDED, this.value);
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.index);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.index = in.readInt();
        this.value = in.readData();
    }
}

