/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionClearBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class CollectionCompareAndRemoveOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    private boolean retain;
    private Set<Data> valueSet;
    private Map<Long, Data> itemIdMap;

    public CollectionCompareAndRemoveOperation() {
    }

    public CollectionCompareAndRemoveOperation(String name, boolean retain, Set<Data> valueSet) {
        super(name);
        this.retain = retain;
        this.valueSet = valueSet;
    }

    @Override
    public boolean shouldBackup() {
        return !this.itemIdMap.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionClearBackupOperation(this.name, this.itemIdMap.keySet());
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.itemIdMap = collectionContainer.compareAndRemove(this.retain, this.valueSet);
        this.response = !this.itemIdMap.isEmpty();
    }

    @Override
    public void afterRun() throws Exception {
        for (Data value : this.itemIdMap.values()) {
            this.publishEvent(ItemEventType.REMOVED, value);
        }
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.retain);
        out.writeInt(this.valueSet.size());
        for (Data value : this.valueSet) {
            out.writeData(value);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.retain = in.readBoolean();
        int size = in.readInt();
        this.valueSet = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data value = in.readData();
            this.valueSet.add(value);
        }
    }
}

