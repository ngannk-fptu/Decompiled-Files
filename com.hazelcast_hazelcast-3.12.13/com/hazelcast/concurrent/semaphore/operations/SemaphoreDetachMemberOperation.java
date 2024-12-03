/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.concurrent.semaphore.SemaphoreWaitNotifyKey;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupAwareOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreDetachMemberBackupOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import java.io.IOException;

public class SemaphoreDetachMemberOperation
extends SemaphoreBackupAwareOperation
implements Notifier {
    private String detachedMemberUuid;

    public SemaphoreDetachMemberOperation() {
    }

    public SemaphoreDetachMemberOperation(String name, String detachedMemberUuid) {
        super(name, -1);
        this.detachedMemberUuid = detachedMemberUuid;
    }

    @Override
    public void run() throws Exception {
        ILogger logger;
        SemaphoreService service = (SemaphoreService)this.getService();
        if (service.containsSemaphore(this.name)) {
            SemaphoreContainer semaphoreContainer = service.getSemaphoreContainer(this.name);
            this.response = semaphoreContainer.detachAll(this.detachedMemberUuid);
        }
        if ((logger = this.getLogger()).isFineEnabled()) {
            logger.fine("Removing permits attached to " + this.detachedMemberUuid + ". Result: " + this.response);
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
    public int getAsyncBackupCount() {
        int syncBackupCount = super.getSyncBackupCount();
        int asyncBackupCount = super.getAsyncBackupCount();
        return syncBackupCount + asyncBackupCount;
    }

    @Override
    public int getSyncBackupCount() {
        return 0;
    }

    @Override
    public Operation getBackupOperation() {
        return new SemaphoreDetachMemberBackupOperation(this.name, this.detachedMemberUuid);
    }

    @Override
    public boolean shouldNotify() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return new SemaphoreWaitNotifyKey(this.name, "acquire");
    }

    @Override
    public int getId() {
        return 13;
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException();
    }
}

