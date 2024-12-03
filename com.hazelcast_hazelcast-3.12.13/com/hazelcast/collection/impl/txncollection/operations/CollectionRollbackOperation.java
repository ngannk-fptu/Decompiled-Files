/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionRollbackBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class CollectionRollbackOperation
extends CollectionBackupAwareOperation {
    private long[] itemIds;

    public CollectionRollbackOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public CollectionRollbackOperation(int partitionId, String name, String serviceName, long[] itemIds) {
        super(name);
        this.setPartitionId(partitionId);
        this.setServiceName(serviceName);
        this.itemIds = itemIds;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionRollbackBackupOperation(this.name, this.itemIds);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        for (long itemId : this.itemIds) {
            if (CollectionTxnUtil.isRemove(itemId)) {
                collectionContainer.rollbackRemove(itemId);
                continue;
            }
            collectionContainer.rollbackAdd(-itemId);
        }
    }

    @Override
    public int getId() {
        return 32;
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

