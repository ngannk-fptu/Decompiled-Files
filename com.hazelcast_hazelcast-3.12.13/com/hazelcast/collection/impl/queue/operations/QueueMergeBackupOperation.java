/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class QueueMergeBackupOperation
extends QueueOperation
implements MutatingOperation {
    private Collection<QueueItem> backupItems;

    public QueueMergeBackupOperation() {
    }

    public QueueMergeBackupOperation(String name, Collection<QueueItem> backupItems) {
        super(name);
        this.backupItems = backupItems;
    }

    @Override
    public void run() {
        if (this.backupItems.isEmpty()) {
            QueueService service = (QueueService)this.getService();
            service.destroyDistributedObject(this.name);
            return;
        }
        QueueContainer container = this.getContainer();
        container.clear();
        Map<Long, QueueItem> backupMap = container.getBackupMap();
        for (QueueItem backupItem : this.backupItems) {
            backupMap.put(backupItem.getItemId(), backupItem);
        }
    }

    @Override
    public void afterRun() {
        this.getQueueService().getLocalQueueStatsImpl(this.name).incrementOtherOperations();
    }

    @Override
    public int getId() {
        return 45;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.backupItems.size());
        for (QueueItem backupItem : this.backupItems) {
            out.writeObject(backupItem);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.backupItems = new ArrayList<QueueItem>(size);
        for (int i = 0; i < size; ++i) {
            QueueItem backupItem = (QueueItem)in.readObject();
            this.backupItems.add(backupItem);
        }
    }
}

