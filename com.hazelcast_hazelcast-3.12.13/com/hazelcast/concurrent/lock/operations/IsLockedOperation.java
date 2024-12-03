/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ReadonlyOperation;

public class IsLockedOperation
extends AbstractLockOperation
implements ReadonlyOperation {
    public IsLockedOperation() {
    }

    public IsLockedOperation(ObjectNamespace namespace, Data key) {
        super(namespace, key, 0L);
    }

    public IsLockedOperation(ObjectNamespace namespace, Data key, long threadId) {
        super(namespace, key, threadId);
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        this.response = this.threadId == 0L ? Boolean.valueOf(lockStore.isLocked(this.key)) : Boolean.valueOf(lockStore.isLockedBy(this.key, this.getCallerUuid(), this.threadId));
    }
}

