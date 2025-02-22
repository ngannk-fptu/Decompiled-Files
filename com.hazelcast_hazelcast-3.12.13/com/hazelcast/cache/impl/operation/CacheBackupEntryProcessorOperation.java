/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;
import javax.cache.processor.EntryProcessor;

public class CacheBackupEntryProcessorOperation
extends KeyBasedCacheOperation
implements BackupOperation,
IdentifiedDataSerializable {
    private EntryProcessor entryProcessor;
    private Object[] arguments;

    public CacheBackupEntryProcessorOperation() {
    }

    public CacheBackupEntryProcessorOperation(String cacheNameWithPrefix, Data key, EntryProcessor entryProcessor, Object ... arguments) {
        super(cacheNameWithPrefix, key);
        this.entryProcessor = entryProcessor;
        this.arguments = arguments;
    }

    @Override
    public int getId() {
        return 33;
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore != null) {
            this.recordStore.invoke(this.key, this.entryProcessor, this.arguments, -1);
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (this.recordStore == null) {
            return;
        }
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

