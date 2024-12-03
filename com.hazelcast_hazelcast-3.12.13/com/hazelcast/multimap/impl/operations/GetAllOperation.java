/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.util.Collection;

public class GetAllOperation
extends AbstractKeyBasedMultiMapOperation
implements BlockingOperation,
ReadonlyOperation {
    public GetAllOperation() {
    }

    public GetAllOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        Collection<MultiMapRecord> coll = null;
        if (multiMapValue != null) {
            multiMapValue.incrementHit();
            coll = multiMapValue.getCollection(this.executedLocally());
        }
        this.response = new MultiMapResponse(coll, this.getValueCollectionType(container));
    }

    @Override
    public int getId() {
        return 11;
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

