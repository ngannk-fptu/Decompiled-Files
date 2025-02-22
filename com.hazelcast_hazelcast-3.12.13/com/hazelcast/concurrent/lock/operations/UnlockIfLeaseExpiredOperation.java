/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.QuorumCheckAwareOperation;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import java.io.IOException;

public final class UnlockIfLeaseExpiredOperation
extends UnlockOperation
implements QuorumCheckAwareOperation {
    private int version;

    public UnlockIfLeaseExpiredOperation() {
    }

    public UnlockIfLeaseExpiredOperation(ObjectNamespace namespace, Data key, int version) {
        super(namespace, key, -1L, true);
        this.version = version;
    }

    @Override
    public void run() {
        LockStoreImpl lockStore = this.getLockStore();
        int lockVersion = lockStore.getVersion(this.key);
        ILogger logger = this.getLogger();
        if (this.version == lockVersion) {
            if (logger.isFinestEnabled()) {
                logger.finest("Releasing a lock owned by " + lockStore.getOwnerInfo(this.key) + " after lease timeout!");
            }
            this.forceUnlock();
        } else if (logger.isFinestEnabled()) {
            logger.finest("Won't unlock since lock version is not matching expiration version: " + lockVersion + " vs " + this.version);
        }
    }

    @Override
    public boolean shouldBackup() {
        IPartition partition;
        NodeEngine nodeEngine = this.getNodeEngine();
        IPartitionService partitionService = nodeEngine.getPartitionService();
        Address thisAddress = nodeEngine.getThisAddress();
        if (!thisAddress.equals((partition = partitionService.getPartition(this.getPartitionId())).getOwnerOrNull())) {
            return false;
        }
        return super.shouldBackup();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.version);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.version = in.readInt();
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    public boolean shouldCheckQuorum() {
        return false;
    }
}

