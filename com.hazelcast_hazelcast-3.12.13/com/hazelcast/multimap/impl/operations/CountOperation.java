/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;

public class CountOperation
extends AbstractKeyBasedMultiMapOperation
implements BlockingOperation,
ReadonlyOperation {
    public CountOperation() {
    }

    public CountOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementOtherOperations();
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        this.response = multiMapValue == null ? 0 : multiMapValue.getCollection(false).size();
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(new DistributedObjectNamespace("hz:impl:multiMapService", this.name), this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        MultiMapContainer container = this.getOrCreateContainer();
        if (container.isTransactionallyLocked(this.dataKey)) {
            return !container.canAcquireLock(this.dataKey, this.getCallerUuid(), this.threadId);
        }
        return false;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(new OperationTimeoutException("Cannot read transactionally locked entry!"));
    }
}

