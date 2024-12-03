/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockResourceImpl;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.QuorumCheckAwareOperation;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import java.io.IOException;

public class LocalLockCleanupOperation
extends UnlockOperation
implements Notifier,
BackupAwareOperation,
QuorumCheckAwareOperation {
    private final String uuid;

    public LocalLockCleanupOperation() {
        this.uuid = "";
    }

    public LocalLockCleanupOperation(ObjectNamespace namespace, Data key, String uuid) {
        super(namespace, key, -1L, true);
        this.uuid = uuid;
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        LockResourceImpl lock = lockStore.getLock(this.key);
        if (this.uuid.equals(lock.getOwner())) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("Unlocking lock owned by UUID: " + this.uuid + ", thread ID: " + lock.getThreadId() + ", count: " + lock.getLockCount());
            }
            this.response = lockStore.forceUnlock(this.key);
        }
    }

    @Override
    public boolean shouldBackup() {
        NodeEngine nodeEngine = this.getNodeEngine();
        IPartitionService partitionService = nodeEngine.getPartitionService();
        IPartition partition = partitionService.getPartition(this.getPartitionId());
        return partition.isLocal() && Boolean.TRUE.equals(this.response);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException("LocalLockCleanupOperation is local only.");
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException("LocalLockCleanupOperation is local only.");
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("LocalLockCleanupOperation is local only.");
    }

    @Override
    public boolean shouldCheckQuorum() {
        return false;
    }
}

