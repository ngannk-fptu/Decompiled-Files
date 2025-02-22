/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class ContainsEntryOperation
extends AbstractMultiMapOperation
implements BlockingOperation,
ReadonlyOperation {
    private Data key;
    private Data value;
    private long threadId;

    public ContainsEntryOperation() {
    }

    public ContainsEntryOperation(String name, Data key, Data value) {
        super(name);
        this.key = key;
        this.value = value;
    }

    public ContainsEntryOperation(String name, Data key, Data value, long threadId) {
        super(name);
        this.key = key;
        this.value = value;
        this.threadId = threadId;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementOtherOperations();
        this.response = this.key != null && this.value != null ? Boolean.valueOf(container.containsEntry(this.isBinary(), this.key, this.value)) : (this.key != null ? Boolean.valueOf(container.containsKey(this.key)) : Boolean.valueOf(container.containsValue(this.isBinary(), this.value)));
    }

    public long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.threadId);
        out.writeData(this.key);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.threadId = in.readLong();
        this.key = in.readData();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(new DistributedObjectNamespace("hz:impl:multiMapService", this.name), this.key);
    }

    @Override
    public boolean shouldWait() {
        if (this.key == null) {
            return false;
        }
        MultiMapContainer container = this.getOrCreateContainer();
        if (container.isTransactionallyLocked(this.key)) {
            return !container.canAcquireLock(this.key, this.getCallerUuid(), this.threadId);
        }
        return false;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(new OperationTimeoutException("Cannot read transactionally locked entry!"));
    }
}

