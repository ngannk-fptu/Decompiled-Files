/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.ClearBackupOperation;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.util.Map;

public class ClearOperation
extends QueueBackupAwareOperation
implements Notifier,
MutatingOperation {
    private Map<Long, Data> dataMap;

    public ClearOperation() {
    }

    public ClearOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        this.dataMap = queueContainer.clear();
        this.response = true;
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
        for (Data data : this.dataMap.values()) {
            this.publishEvent(ItemEventType.REMOVED, data);
        }
    }

    @Override
    public Operation getBackupOperation() {
        return new ClearBackupOperation(this.name, this.dataMap.keySet());
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(!this.dataMap.isEmpty());
    }

    @Override
    public boolean shouldNotify() {
        return Boolean.TRUE.equals(!this.dataMap.isEmpty());
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getContainer().getOfferWaitNotifyKey();
    }

    @Override
    public int getId() {
        return 8;
    }
}

