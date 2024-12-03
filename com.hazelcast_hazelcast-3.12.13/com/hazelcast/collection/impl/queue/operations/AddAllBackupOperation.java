/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class AddAllBackupOperation
extends QueueOperation
implements BackupOperation {
    private Map<Long, Data> dataMap;

    public AddAllBackupOperation() {
    }

    public AddAllBackupOperation(String name, Map<Long, Data> dataMap) {
        super(name);
        this.dataMap = dataMap;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.addAllBackup(this.dataMap);
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.dataMap.size());
        for (Map.Entry<Long, Data> entry : this.dataMap.entrySet()) {
            long itemId = entry.getKey();
            Data value = entry.getValue();
            out.writeLong(itemId);
            out.writeData(value);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.dataMap = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            long itemId = in.readLong();
            Data value = in.readData();
            this.dataMap.put(itemId, value);
        }
    }
}

