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

public class CollectionPrepareBackupOperation
extends CollectionOperation
implements BackupOperation {
    private long[] itemIds;
    private String transactionId;

    public CollectionPrepareBackupOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public CollectionPrepareBackupOperation(String name, long[] itemIds, String transactionId) {
        super(name);
        this.itemIds = itemIds;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        for (long itemId : this.itemIds) {
            if (CollectionTxnUtil.isRemove(itemId)) {
                collectionContainer.reserveRemoveBackup(itemId, this.transactionId);
                continue;
            }
            collectionContainer.reserveAddBackup(-itemId, this.transactionId);
        }
    }

    @Override
    public int getId() {
        return 31;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.transactionId);
        out.writeLongArray(this.itemIds);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.transactionId = in.readUTF();
        this.itemIds = in.readLongArray();
    }
}

