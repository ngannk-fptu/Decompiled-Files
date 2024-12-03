/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PutFromLoadAllBackupOperation
extends MapOperation
implements BackupOperation {
    private List<Data> keyValueSequence;

    public PutFromLoadAllBackupOperation() {
        this.keyValueSequence = Collections.emptyList();
    }

    public PutFromLoadAllBackupOperation(String name, List<Data> keyValueSequence) {
        super(name);
        this.keyValueSequence = keyValueSequence;
    }

    @Override
    public void run() throws Exception {
        List<Data> keyValueSequence = this.keyValueSequence;
        if (keyValueSequence == null || keyValueSequence.isEmpty()) {
            return;
        }
        for (int i = 0; i < keyValueSequence.size(); i += 2) {
            Data key = keyValueSequence.get(i);
            Data value = keyValueSequence.get(i + 1);
            Object object = this.mapServiceContext.toObject(value);
            this.recordStore.putFromLoadBackup(key, object);
            if (!this.recordStore.existInMemory(key)) continue;
            this.publishLoadAsWanUpdate(key, value);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.evict(null);
        super.afterRun();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        List<Data> keyValueSequence = this.keyValueSequence;
        int size = keyValueSequence.size();
        out.writeInt(size);
        for (Data data : keyValueSequence) {
            out.writeData(data);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        if (size < 1) {
            this.keyValueSequence = Collections.emptyList();
        } else {
            ArrayList<Data> tmpKeyValueSequence = new ArrayList<Data>(size);
            for (int i = 0; i < size; ++i) {
                Data data = in.readData();
                tmpKeyValueSequence.add(data);
            }
            this.keyValueSequence = tmpKeyValueSequence;
        }
    }

    @Override
    public int getId() {
        return 58;
    }
}

