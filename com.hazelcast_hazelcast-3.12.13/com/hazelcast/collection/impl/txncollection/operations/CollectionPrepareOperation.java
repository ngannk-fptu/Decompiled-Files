/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.collection.impl.txncollection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionPrepareBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class CollectionPrepareOperation
extends CollectionBackupAwareOperation {
    private String transactionId;
    private long[] itemIds;

    public CollectionPrepareOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public CollectionPrepareOperation(int partitionId, String name, String serviceName, long[] itemIds, String transactionId) {
        super(name);
        this.setPartitionId(partitionId);
        this.setServiceName(serviceName);
        this.itemIds = itemIds;
        this.transactionId = transactionId;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionPrepareBackupOperation(this.name, this.itemIds, this.transactionId);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        for (long itemId : this.itemIds) {
            collectionContainer.ensureReserve(Math.abs(itemId));
        }
    }

    @Override
    public int getId() {
        return 30;
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

