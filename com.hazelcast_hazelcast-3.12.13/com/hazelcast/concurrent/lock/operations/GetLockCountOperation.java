/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ReadonlyOperation;

public class GetLockCountOperation
extends AbstractLockOperation
implements ReadonlyOperation {
    public GetLockCountOperation() {
    }

    public GetLockCountOperation(ObjectNamespace namespace, Data key) {
        super(namespace, key, -1L);
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        this.response = lockStore.getLockCount(this.key);
    }
}

