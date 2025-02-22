/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.util.TimeUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class EvictBatchBackupOperation
extends MapOperation
implements BackupOperation {
    private int primaryEntryCount;
    private String name;
    private Collection<ExpiredKey> expiredKeys;

    public EvictBatchBackupOperation() {
    }

    public EvictBatchBackupOperation(String name, Collection<ExpiredKey> expiredKeys, int primaryEntryCount) {
        super(name);
        assert (name != null);
        this.name = name;
        this.expiredKeys = expiredKeys;
        this.primaryEntryCount = primaryEntryCount;
        this.createRecordStoreOnDemand = false;
    }

    @Override
    public void run() {
        if (this.recordStore == null) {
            return;
        }
        for (ExpiredKey expiredKey : this.expiredKeys) {
            Object existingRecord = this.recordStore.getRecord(expiredKey.getKey());
            if (!this.canEvictRecord((Record)existingRecord, expiredKey)) continue;
            this.recordStore.evict(existingRecord.getKey(), true);
        }
        this.equalizeEntryCountWithPrimary();
    }

    @Override
    public void afterRun() throws Exception {
        try {
            super.afterRun();
        }
        finally {
            this.recordStore.disposeDeferredBlocks();
        }
    }

    private void equalizeEntryCountWithPrimary() {
        int diff = this.recordStore.size() - this.primaryEntryCount;
        if (diff <= 0) {
            return;
        }
        this.recordStore.sampleAndForceRemoveEntries(diff);
        assert (this.recordStore.size() == this.primaryEntryCount) : String.format("Failed to remove %d entries while attempting to match primary entry count %d, recordStore size is now %d", diff, this.primaryEntryCount, this.recordStore.size());
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof WrongTargetException && ((WrongTargetException)throwable).getTarget() == null) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    private boolean canEvictRecord(Record existingRecord, ExpiredKey expiredKey) {
        if (existingRecord == null) {
            return false;
        }
        return existingRecord.getCreationTime() == TimeUtil.zeroOutMs(expiredKey.getCreationTime());
    }

    @Override
    public int getId() {
        return 140;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.name);
        out.writeInt(this.expiredKeys.size());
        for (ExpiredKey expiredKey : this.expiredKeys) {
            out.writeData(expiredKey.getKey());
            out.writeLong(expiredKey.getCreationTime());
        }
        out.writeInt(this.primaryEntryCount);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.name = in.readUTF();
        int size = in.readInt();
        this.expiredKeys = new LinkedList<ExpiredKey>();
        for (int i = 0; i < size; ++i) {
            this.expiredKeys.add(new ExpiredKey(in.readData(), in.readLong()));
        }
        this.primaryEntryCount = in.readInt();
    }
}

