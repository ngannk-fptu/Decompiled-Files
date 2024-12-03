/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.AbstractMultipleEntryBackupOperation;
import com.hazelcast.map.impl.operation.EntryOperator;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class MultipleEntryBackupOperation
extends AbstractMultipleEntryBackupOperation
implements BackupOperation {
    private Set<Data> keys;

    public MultipleEntryBackupOperation() {
    }

    public MultipleEntryBackupOperation(String name, Set<Data> keys, EntryBackupProcessor backupProcessor) {
        super(name, backupProcessor);
        this.keys = keys;
    }

    @Override
    public void run() throws Exception {
        EntryOperator operator = EntryOperator.operator(this, this.backupProcessor, this.getPredicate());
        for (Data key : this.keys) {
            operator.operateOnKey(key).doPostOperateOps();
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.backupProcessor = (EntryBackupProcessor)in.readObject();
        int size = in.readInt();
        this.keys = SetUtil.createLinkedHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            this.keys.add(key);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.backupProcessor);
        out.writeInt(this.keys.size());
        for (Data key : this.keys) {
            out.writeData(key);
        }
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public int getId() {
        return 51;
    }
}

