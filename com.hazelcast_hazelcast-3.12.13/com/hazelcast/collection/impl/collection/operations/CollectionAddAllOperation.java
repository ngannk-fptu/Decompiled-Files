/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionAddAllBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionAddAllOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    protected List<Data> valueList;
    protected Map<Long, Data> valueMap;

    public CollectionAddAllOperation() {
    }

    public CollectionAddAllOperation(String name, List<Data> valueList) {
        super(name);
        this.valueList = valueList;
    }

    @Override
    public boolean shouldBackup() {
        return this.valueMap != null && !this.valueMap.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionAddAllBackupOperation(this.name, this.valueMap);
    }

    @Override
    public void run() throws Exception {
        if (!this.hasEnoughCapacity(this.valueList.size())) {
            this.response = false;
            return;
        }
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.valueMap = collectionContainer.addAll(this.valueList);
        this.response = !this.valueMap.isEmpty();
    }

    @Override
    public void afterRun() throws Exception {
        if (this.valueMap == null) {
            return;
        }
        for (Data value : this.valueMap.values()) {
            this.publishEvent(ItemEventType.ADDED, value);
        }
    }

    @Override
    public int getId() {
        return 15;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.valueList.size());
        for (Data value : this.valueList) {
            out.writeData(value);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.valueList = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            Data value = in.readData();
            this.valueList.add(value);
        }
    }
}

