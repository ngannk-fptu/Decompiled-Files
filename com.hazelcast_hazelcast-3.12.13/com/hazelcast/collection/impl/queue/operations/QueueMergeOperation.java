/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.queue.operations.QueueMergeBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.CollectionUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.Queue;

public class QueueMergeOperation
extends QueueBackupAwareOperation
implements MutatingOperation {
    private SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.QueueMergeTypes> mergePolicy;
    private SplitBrainMergeTypes.QueueMergeTypes mergingValue;
    private transient Collection<QueueItem> backupCollection;
    private transient boolean shouldBackup;

    public QueueMergeOperation() {
    }

    public QueueMergeOperation(String name, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.QueueMergeTypes> mergePolicy, SplitBrainMergeTypes.QueueMergeTypes mergingValue) {
        super(name);
        this.mergePolicy = mergePolicy;
        this.mergingValue = mergingValue;
    }

    @Override
    public void run() {
        QueueContainer container = this.getContainer();
        boolean currentCollectionIsEmpty = container.getItemQueue().isEmpty();
        long currentItemId = container.getCurrentId();
        this.backupCollection = this.merge(container, this.mergingValue, this.mergePolicy);
        this.shouldBackup = currentCollectionIsEmpty != this.backupCollection.isEmpty() || currentItemId != container.getCurrentId();
    }

    private Queue<QueueItem> merge(QueueContainer container, SplitBrainMergeTypes.QueueMergeTypes mergingValue, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.QueueMergeTypes> mergePolicy) {
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        serializationService.getManagedContext().initialize(mergingValue);
        serializationService.getManagedContext().initialize(mergePolicy);
        Deque<QueueItem> existingItems = container.getItemQueue();
        SplitBrainMergeTypes.QueueMergeTypes existingValue = this.createMergingValueOrNull(serializationService, existingItems);
        Collection<Object> newValues = mergePolicy.merge(mergingValue, existingValue);
        if (CollectionUtil.isEmpty(newValues)) {
            if (existingValue != null) {
                container.clear();
            }
            this.getQueueService().destroyDistributedObject(this.name);
        } else if (existingValue == null) {
            this.createNewQueueItems(container, newValues, serializationService);
        } else if (!newValues.equals(existingValue.getValue())) {
            container.clear();
            this.createNewQueueItems(container, newValues, serializationService);
        }
        return existingItems;
    }

    private SplitBrainMergeTypes.QueueMergeTypes createMergingValueOrNull(SerializationService serializationService, Queue<QueueItem> existingItems) {
        return existingItems.isEmpty() ? null : MergingValueFactory.createMergingValue(serializationService, existingItems);
    }

    private void createNewQueueItems(QueueContainer container, Collection<Object> values, SerializationService serializationService) {
        for (Object value : values) {
            container.offer((Data)serializationService.toData(value));
        }
    }

    @Override
    public void afterRun() {
        this.getQueueService().getLocalQueueStatsImpl(this.name).incrementOtherOperations();
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public Operation getBackupOperation() {
        return new QueueMergeBackupOperation(this.name, this.backupCollection);
    }

    @Override
    public int getId() {
        return 44;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.mergePolicy);
        out.writeObject(this.mergingValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        this.mergingValue = (SplitBrainMergeTypes.QueueMergeTypes)in.readObject();
    }
}

