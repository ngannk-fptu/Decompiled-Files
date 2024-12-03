/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class CollectionRollbackBackupOperation
extends CollectionOperation
implements BackupOperation {
    private long[] itemIds;

    public CollectionRollbackBackupOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public CollectionRollbackBackupOperation(String name, long[] itemIds) {
        super(name);
        this.itemIds = itemIds;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        for (long itemId : this.itemIds) {
            if (CollectionTxnUtil.isRemove(itemId)) {
                collectionContainer.rollbackRemoveBackup(itemId);
                continue;
            }
            collectionContainer.rollbackAddBackup(-itemId);
        }
    }

    @Override
    public int getId() {
        return 33;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLongArray(this.itemIds);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemIds = in.readLongArray();
    }
}

