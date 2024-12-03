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

public class DrainBackupOperation
extends QueueOperation
implements BackupOperation {
    private Set<Long> itemIdSet;

    public DrainBackupOperation() {
    }

    public DrainBackupOperation(String name, Set<Long> itemIdSet) {
        super(name);
        this.itemIdSet = itemIdSet;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.drainFromBackup(this.itemIdSet);
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        if (this.itemIdSet == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(this.itemIdSet.size());
            for (Long itemId : this.itemIdSet) {
                out.writeLong(itemId);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        if (in.readBoolean()) {
            int size = in.readInt();
            this.itemIdSet = SetUtil.createHashSet(size);
            for (int i = 0; i < size; ++i) {
                this.itemIdSet.add(in.readLong());
            }
        }
    }
}

