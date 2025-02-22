/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.AbstractMultipleEntryBackupOperation;
import com.hazelcast.map.impl.operation.EntryOperator;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.Clock;
import java.io.IOException;
import java.util.Iterator;

public class PartitionWideEntryBackupOperation
extends AbstractMultipleEntryBackupOperation
implements BackupOperation {
    public PartitionWideEntryBackupOperation() {
    }

    public PartitionWideEntryBackupOperation(String name, EntryBackupProcessor backupProcessor) {
        super(name, backupProcessor);
    }

    @Override
    public void run() {
        EntryOperator operator = EntryOperator.operator(this, this.backupProcessor, this.getPredicate());
        Iterator<Record> iterator = this.recordStore.iterator(Clock.currentTimeMillis(), true);
        while (iterator.hasNext()) {
            Record record = iterator.next();
            operator.operateOnKey(record.getKey()).doPostOperateOps();
        }
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.backupProcessor = (EntryBackupProcessor)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.backupProcessor);
    }

    @Override
    public int getId() {
        return 40;
    }
}

