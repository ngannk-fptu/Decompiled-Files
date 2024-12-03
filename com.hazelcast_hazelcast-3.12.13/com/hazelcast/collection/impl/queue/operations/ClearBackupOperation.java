/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class ClearBackupOperation
extends QueueOperation
implements BackupOperation {
    private Set<Long> itemIdSet;

    public ClearBackupOperation() {
    }

    public ClearBackupOperation(String name, Set<Long> itemIdSet) {
        super(name);
        this.itemIdSet = itemIdSet;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.clearBackup(this.itemIdSet);
        this.response = true;
    }

    @Override
    public int getId() {
        return 7;
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

