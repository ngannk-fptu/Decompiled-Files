/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class CollectionAddAllBackupOperation
extends CollectionOperation
implements BackupOperation {
    protected Map<Long, Data> valueMap;

    public CollectionAddAllBackupOperation() {
    }

    public CollectionAddAllBackupOperation(String name, Map<Long, Data> valueMap) {
        super(name);
        this.valueMap = valueMap;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        collectionContainer.addAllBackup(this.valueMap);
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.valueMap.size());
        for (Map.Entry<Long, Data> entry : this.valueMap.entrySet()) {
            out.writeLong(entry.getKey());
            out.writeData(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.valueMap = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            long itemId = in.readLong();
            Data value = in.readData();
            this.valueMap.put(itemId, value);
        }
    }
}

