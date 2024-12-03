/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class CollectionClearBackupOperation
extends CollectionOperation
implements BackupOperation {
    private Set<Long> itemIdSet;

    public CollectionClearBackupOperation() {
    }

    public CollectionClearBackupOperation(String name, Set<Long> itemIdSet) {
        super(name);
        this.itemIdSet = itemIdSet;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        collectionContainer.clearBackup(this.itemIdSet);
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.itemIdSet.size());
        for (Long itemId : this.itemIdSet) {
            out.writeLong(itemId);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.itemIdSet = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            this.itemIdSet.add(in.readLong());
        }
    }
}

