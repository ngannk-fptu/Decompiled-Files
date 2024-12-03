/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionMergeBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.CollectionUtil;
import java.io.IOException;
import java.util.Collection;

public class CollectionMergeOperation
extends CollectionBackupAwareOperation {
    private SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.CollectionMergeTypes> mergePolicy;
    private SplitBrainMergeTypes.CollectionMergeTypes mergingValue;
    private transient Collection<CollectionItem> backupCollection;
    private transient boolean shouldBackup;

    public CollectionMergeOperation(String name, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.CollectionMergeTypes> mergePolicy, SplitBrainMergeTypes.CollectionMergeTypes mergingValue) {
        super(name);
        this.mergePolicy = mergePolicy;
        this.mergingValue = mergingValue;
    }

    public CollectionMergeOperation() {
    }

    @Override
    public void run() throws Exception {
        CollectionContainer container = this.getOrCreateContainer();
        boolean currentCollectionIsEmpty = container.getCollection().isEmpty();
        long currentItemId = container.getCurrentId();
        this.backupCollection = this.merge(container, this.mergingValue, this.mergePolicy);
        this.shouldBackup = currentCollectionIsEmpty != this.backupCollection.isEmpty() || currentItemId != container.getCurrentId();
    }

    private Collection<CollectionItem> merge(CollectionContainer container, SplitBrainMergeTypes.CollectionMergeTypes mergingValue, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.CollectionMergeTypes> mergePolicy) {
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        serializationService.getManagedContext().initialize(mergingValue);
        serializationService.getManagedContext().initialize(mergePolicy);
        Collection<CollectionItem> existingItems = container.getCollection();
        SplitBrainMergeTypes.CollectionMergeTypes existingValue = this.createMergingValueOrNull(serializationService, existingItems);
        Collection<Object> newValues = mergePolicy.merge(mergingValue, existingValue);
        if (CollectionUtil.isEmpty(newValues)) {
            RemoteService service = (RemoteService)this.getService();
            service.destroyDistributedObject(this.name);
        } else if (existingValue == null) {
            this.createNewCollectionItems(container, existingItems, newValues, serializationService);
        } else if (!newValues.equals(existingValue.getValue())) {
            container.clear(false);
            this.createNewCollectionItems(container, existingItems, newValues, serializationService);
        }
        return existingItems;
    }

    private SplitBrainMergeTypes.CollectionMergeTypes createMergingValueOrNull(SerializationService serializationService, Collection<CollectionItem> existingItems) {
        return existingItems.isEmpty() ? null : MergingValueFactory.createMergingValue(serializationService, existingItems);
    }

    private void createNewCollectionItems(CollectionContainer container, Collection<CollectionItem> items, Collection<Object> values, SerializationService serializationService) {
        for (Object value : values) {
            CollectionItem item = new CollectionItem(container.nextId(), (Data)serializationService.toData(value));
            items.add(item);
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionMergeBackupOperation(this.name, this.backupCollection);
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
        this.mergingValue = (SplitBrainMergeTypes.CollectionMergeTypes)in.readObject();
    }

    @Override
    public int getId() {
        return 45;
    }
}

