/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.BackupAwareEntryProcessor;
import com.hazelcast.cache.impl.operation.CacheBackupEntryProcessorOperation;
import com.hazelcast.cache.impl.operation.CachePutBackupOperation;
import com.hazelcast.cache.impl.operation.CacheRemoveBackupOperation;
import com.hazelcast.cache.impl.operation.MutatingCacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import javax.cache.processor.EntryProcessor;

public class CacheEntryProcessorOperation
extends MutatingCacheOperation {
    private EntryProcessor entryProcessor;
    private Object[] arguments;
    private transient CacheRecord backupRecord;
    private transient EntryProcessor backupEntryProcessor;

    public CacheEntryProcessorOperation() {
    }

    public CacheEntryProcessorOperation(String cacheNameWithPrefix, Data key, int completionId, EntryProcessor entryProcessor, Object ... arguments) {
        super(cacheNameWithPrefix, key, completionId);
        this.entryProcessor = entryProcessor;
        this.arguments = arguments;
        this.completionId = completionId;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        if (this.backupEntryProcessor != null) {
            return new CacheBackupEntryProcessorOperation(this.name, this.key, this.backupEntryProcessor, this.arguments);
        }
        if (this.backupRecord != null) {
            return new CachePutBackupOperation(this.name, this.key, this.backupRecord);
        }
        return new CacheRemoveBackupOperation(this.name, this.key);
    }

    @Override
    public int getId() {
        return 24;
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.invoke(this.key, this.entryProcessor, this.arguments, this.completionId);
        if (this.entryProcessor instanceof BackupAwareEntryProcessor) {
            BackupAwareEntryProcessor processor = (BackupAwareEntryProcessor)this.entryProcessor;
            this.backupEntryProcessor = processor.createBackupEntryProcessor();
        }
        if (this.backupEntryProcessor == null) {
            this.backupRecord = this.recordStore.getRecord(this.key);
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (this.recordStore.isWanReplicationEnabled()) {
            CacheRecord record = this.recordStore.getRecord(this.key);
            if (record != null) {
                this.publishWanUpdate(this.key, record);
            } else {
                this.publishWanRemove(this.key);
            }
        }
        super.afterRun();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.entryProcessor);
        out.writeBoolean(this.arguments != null);
        if (this.arguments != null) {
            out.writeInt(this.arguments.length);
            for (Object arg : this.arguments) {
                out.writeObject(arg);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.entryProcessor = (EntryProcessor)in.readObject();
        boolean hasArguments = in.readBoolean();
        if (hasArguments) {
            int size = in.readInt();
            this.arguments = new Object[size];
            for (int i = 0; i < size; ++i) {
                this.arguments[i] = in.readObject();
            }
        }
    }
}

