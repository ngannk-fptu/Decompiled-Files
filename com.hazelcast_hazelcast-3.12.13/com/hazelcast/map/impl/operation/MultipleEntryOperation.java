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
import com.hazelcast.map.impl.operation.MultipleEntryBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class MultipleEntryOperation
extends MapOperation
implements MutatingOperation,
PartitionAwareOperation,
BackupAwareOperation {
    protected Set<Data> keys;
    protected MapEntries responses;
    protected EntryProcessor entryProcessor;

    public MultipleEntryOperation() {
    }

    public MultipleEntryOperation(String name, Set<Data> keys, EntryProcessor entryProcessor) {
        super(name);
        this.keys = keys;
        this.entryProcessor = entryProcessor;
    }

    @Override
    public void innerBeforeRun() throws Exception {
        super.innerBeforeRun();
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        ManagedContext managedContext = serializationService.getManagedContext();
        managedContext.initialize(this.entryProcessor);
    }

    @Override
    public void run() throws Exception {
        this.responses = new MapEntries(this.keys.size());
        if (this.keys.isEmpty()) {
            return;
        }
        EntryOperator operator = EntryOperator.operator(this, this.entryProcessor, this.getPredicate());
        for (Data key : this.keys) {
            Data response = operator.operateOnKey(key).doPostOperateOps().getResult();
            if (response == null) continue;
            this.responses.add(key, response);
        }
    }

    protected Predicate getPredicate() {
        return null;
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
        MultipleEntryBackupOperation backupOperation = null;
        if (backupProcessor != null) {
            backupOperation = new MultipleEntryBackupOperation(this.name, this.keys, backupProcessor);
        }
        return backupOperation;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.entryProcessor = (EntryProcessor)in.readObject();
        int size = in.readInt();
        this.keys = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            this.keys.add(key);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.entryProcessor);
        out.writeInt(this.keys.size());
        for (Data key : this.keys) {
            out.writeData(key);
        }
    }

    @Override
    public int getId() {
        return 52;
    }
}

