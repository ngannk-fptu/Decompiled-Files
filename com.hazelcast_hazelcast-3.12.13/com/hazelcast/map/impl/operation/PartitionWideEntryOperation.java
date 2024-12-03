/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.EntryOperator;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryBackupOperation;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import java.io.IOException;
import java.util.Iterator;

public class PartitionWideEntryOperation
extends MapOperation
implements MutatingOperation,
PartitionAwareOperation,
BackupAwareOperation {
    protected MapEntries responses;
    protected EntryProcessor entryProcessor;
    protected transient EntryOperator operator;

    public PartitionWideEntryOperation() {
    }

    public PartitionWideEntryOperation(String name, EntryProcessor entryProcessor) {
        super(name);
        this.entryProcessor = entryProcessor;
    }

    @Override
    public void innerBeforeRun() throws Exception {
        super.innerBeforeRun();
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        ManagedContext managedContext = serializationService.getManagedContext();
        managedContext.initialize(this.entryProcessor);
    }

    protected Predicate getPredicate() {
        return null;
    }

    @Override
    public void run() {
        this.responses = new MapEntries(this.recordStore.size());
        this.operator = EntryOperator.operator(this, this.entryProcessor, this.getPredicate());
        Iterator<Record> iterator = this.recordStore.iterator(Clock.currentTimeMillis(), false);
        while (iterator.hasNext()) {
            Record record = iterator.next();
            Data dataKey = record.getKey();
            Data response = this.operator.operateOnKey(dataKey).doPostOperateOps().getResult();
            if (response == null) continue;
            this.responses.add(dataKey, response);
        }
    }

    @Override
    public Object getResponse() {
        return this.responses;
    }

    @Override
    public boolean shouldBackup() {
        return this.mapContainer.getTotalBackupCount() > 0 && this.entryProcessor.getBackupProcessor() != null;
    }

    @Override
    public int getSyncBackupCount() {
        return 0;
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapContainer.getTotalBackupCount();
    }

    @Override
    public Operation getBackupOperation() {
        EntryBackupProcessor backupProcessor = this.entryProcessor.getBackupProcessor();
        PartitionWideEntryBackupOperation backupOperation = null;
        if (backupProcessor != null) {
            backupOperation = new PartitionWideEntryBackupOperation(this.name, backupProcessor);
        }
        return backupOperation;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", entryProcessor=").append(this.entryProcessor);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.entryProcessor = (EntryProcessor)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.entryProcessor);
    }

    @Override
    public int getId() {
        return 39;
    }
}

