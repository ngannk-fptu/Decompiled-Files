/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class MergeBackupOperation
extends AbstractMultiMapOperation
implements BackupOperation {
    private Map<Data, Collection<MultiMapRecord>> backupEntries;

    public MergeBackupOperation() {
    }

    MergeBackupOperation(String name, Map<Data, Collection<MultiMapRecord>> backupEntries) {
        super(name);
        this.backupEntries = backupEntries;
    }

    @Override
    public void run() throws Exception {
        this.response = true;
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        for (Map.Entry<Data, Collection<MultiMapRecord>> entry : this.backupEntries.entrySet()) {
            Data key = entry.getKey();
            Collection<MultiMapRecord> value = entry.getValue();
            if (value.isEmpty()) {
                container.remove(key, false);
                continue;
            }
            MultiMapValue containerValue = container.getOrCreateMultiMapValue(key);
            Collection<MultiMapRecord> collection = containerValue.getCollection(false);
            collection.clear();
            if (collection.addAll(value)) continue;
            this.response = false;
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.backupEntries.size());
        for (Map.Entry<Data, Collection<MultiMapRecord>> entry : this.backupEntries.entrySet()) {
            out.writeData(entry.getKey());
            Collection<MultiMapRecord> collection = entry.getValue();
            out.writeInt(collection.size());
            for (MultiMapRecord record : collection) {
                out.writeObject(record);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.backupEntries = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            int collectionSize = in.readInt();
            ArrayList<MultiMapRecord> collection = new ArrayList<MultiMapRecord>(collectionSize);
            for (int j = 0; j < collectionSize; ++j) {
                MultiMapRecord record = (MultiMapRecord)in.readObject();
                collection.add(record);
            }
            this.backupEntries.put(key, collection);
        }
    }

    @Override
    public int getId() {
        return 50;
    }
}

