/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.map.impl.operation.LockAwareOperation;
import com.hazelcast.map.impl.tx.VersionedValue;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnLockAndGetOperation
extends LockAwareOperation
implements MutatingOperation {
    private VersionedValue response;
    private String ownerUuid;
    private boolean shouldLoad;
    private boolean blockReads;

    public TxnLockAndGetOperation() {
    }

    public TxnLockAndGetOperation(String name, Data dataKey, long timeout, long ttl, String ownerUuid, boolean shouldLoad, boolean blockReads) {
        super(name, dataKey, ttl, -1L);
        this.ownerUuid = ownerUuid;
        this.shouldLoad = shouldLoad;
        this.blockReads = blockReads;
        this.setWaitTimeout(timeout);
    }

    @Override
    public void run() throws Exception {
        if (!this.recordStore.txnLock(this.getKey(), this.ownerUuid, this.getThreadId(), this.getCallId(), this.ttl, this.blockReads)) {
            throw new TransactionException("Transaction couldn't obtain lock.");
        }
        Object record = this.recordStore.getRecordOrNull(this.dataKey);
        if (record == null && this.shouldLoad) {
            record = this.recordStore.loadRecordOrNull(this.dataKey, false, this.getCallerAddress());
        }
        Data value = record == null ? null : this.mapServiceContext.toData(record.getValue());
        this.response = new VersionedValue(value, record == null ? 0L : record.getVersion());
    }

    @Override
    public boolean shouldWait() {
        return !this.recordStore.canAcquireLock(this.dataKey, this.ownerUuid, this.getThreadId());
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.ownerUuid);
        out.writeBoolean(this.shouldLoad);
        out.writeBoolean(this.blockReads);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.ownerUuid = in.readUTF();
        this.shouldLoad = in.readBoolean();
        this.blockReads = in.readBoolean();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", thread=").append(this.getThreadId());
    }

    @Override
    public int getId() {
        return 65;
    }
}

