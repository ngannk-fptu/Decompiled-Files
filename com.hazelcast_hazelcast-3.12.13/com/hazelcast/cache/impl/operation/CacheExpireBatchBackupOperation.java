/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.WrongTargetException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class CacheExpireBatchBackupOperation
extends CacheOperation {
    private int primaryEntryCount;
    private Collection<ExpiredKey> expiredKeys;

    public CacheExpireBatchBackupOperation() {
    }

    public CacheExpireBatchBackupOperation(String name, Collection<ExpiredKey> expiredKeys, int primaryEntryCount) {
        super(name, true);
        this.expiredKeys = expiredKeys;
        this.primaryEntryCount = primaryEntryCount;
    }

    @Override
    public void run() {
        if (this.recordStore == null) {
            return;
        }
        for (ExpiredKey expiredKey : this.expiredKeys) {
            this.evictIfSame(expiredKey);
        }
        this.equalizeEntryCountWithPrimary();
    }

    private void equalizeEntryCountWithPrimary() {
        int diff = this.recordStore.size() - this.primaryEntryCount;
        if (diff > 0) {
            this.recordStore.sampleAndForceRemoveEntries(diff);
            assert (this.recordStore.size() == this.primaryEntryCount) : String.format("Failed to remove %d entries while attempting to match primary entry count %d, recordStore size is now %d", diff, this.primaryEntryCount, this.recordStore.size());
        }
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

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof WrongTargetException && ((WrongTargetException)throwable).getTarget() == null) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    protected void evictIfSame(ExpiredKey key) {
        CacheRecord record = this.recordStore.getRecord(key.getKey());
        if (record != null && record.getCreationTime() == key.getCreationTime()) {
            this.recordStore.removeRecord(key.getKey());
        }
    }

    @Override
    public int getId() {
        return 69;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
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
        int size = in.readInt();
        this.expiredKeys = new LinkedList<ExpiredKey>();
        for (int i = 0; i < size; ++i) {
            this.expiredKeys.add(new ExpiredKey(in.readData(), in.readLong()));
        }
        this.primaryEntryCount = in.readInt();
    }
}

