/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class ListSetBackupOperation
extends CollectionOperation
implements BackupOperation {
    private long oldItemId;
    private long itemId;
    private Data value;

    public ListSetBackupOperation() {
    }

    public ListSetBackupOperation(String name, long oldItemId, long itemId, Data value) {
        super(name);
        this.oldItemId = oldItemId;
        this.itemId = itemId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        listContainer.setBackup(this.oldItemId, this.itemId, this.value);
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.oldItemId);
        out.writeLong(this.itemId);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.oldItemId = in.readLong();
        this.itemId = in.readLong();
        this.value = in.readData();
    }
}

