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

public class CompareAndRemoveBackupOperation
extends QueueOperation
implements BackupOperation {
    private Set<Long> keySet;

    public CompareAndRemoveBackupOperation() {
    }

    public CompareAndRemoveBackupOperation(String name, Set<Long> keySet) {
        super(name);
        this.keySet = keySet;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.compareAndRemoveBackup(this.keySet);
        this.response = true;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.keySet.size());
        for (Long key : this.keySet) {
            out.writeLong(key);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.keySet = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            this.keySet.add(in.readLong());
        }
    }
}

