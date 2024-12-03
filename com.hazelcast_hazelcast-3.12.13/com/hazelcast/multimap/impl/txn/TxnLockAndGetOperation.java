/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;
import java.util.Collection;

public class TxnLockAndGetOperation
extends AbstractKeyBasedMultiMapOperation
implements BlockingOperation,
MutatingOperation {
    private long ttl;
    private boolean blockReads;

    public TxnLockAndGetOperation() {
    }

    public TxnLockAndGetOperation(String name, Data dataKey, long timeout, long ttl, long threadId, boolean blockReads) {
        super(name, dataKey);
        this.ttl = ttl;
        this.threadId = threadId;
        this.blockReads = blockReads;
        this.setWaitTimeout(timeout);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        if (!container.txnLock(this.dataKey, this.getCallerUuid(), this.threadId, this.getCallId(), this.ttl, this.blockReads)) {
            throw new TransactionException("Transaction couldn't obtain lock!");
        }
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        boolean isLocal = this.executedLocally();
        Collection<MultiMapRecord> collection = multiMapValue == null ? null : multiMapValue.getCollection(isLocal);
        MultiMapResponse multiMapResponse = new MultiMapResponse(collection, this.getValueCollectionType(container));
        multiMapResponse.setNextRecordId(container.nextId());
        this.response = multiMapResponse;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(new DistributedObjectNamespace("hz:impl:multiMapService", this.name), this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        return !this.getOrCreateContainer().canAcquireLock(this.dataKey, this.getCallerUuid(), this.threadId);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.ttl);
        out.writeBoolean(this.blockReads);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.ttl = in.readLong();
        this.blockReads = in.readBoolean();
    }

    @Override
    public int getId() {
        return 30;
    }
}

