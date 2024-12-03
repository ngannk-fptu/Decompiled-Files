/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.EntryBackupOperation;
import com.hazelcast.map.impl.operation.EntryOffloadableLockMismatchException;
import com.hazelcast.map.impl.operation.EntryOperator;
import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class EntryOffloadableSetUnlockOperation
extends KeyBasedMapOperation
implements BackupAwareOperation,
Notifier {
    protected Data newValue;
    protected Data oldValue;
    protected String caller;
    protected long begin;
    protected EntryEventType modificationType;
    protected EntryBackupProcessor entryBackupProcessor;

    public EntryOffloadableSetUnlockOperation() {
    }

    public EntryOffloadableSetUnlockOperation(String name, EntryEventType modificationType, Data key, Data oldValue, Data newValue, String caller, long threadId, long begin, EntryBackupProcessor entryBackupProcessor) {
        super(name, key, newValue);
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.caller = caller;
        this.begin = begin;
        this.modificationType = modificationType;
        this.entryBackupProcessor = entryBackupProcessor;
        this.setThreadId(threadId);
    }

    @Override
    public void run() throws Exception {
        this.verifyLock();
        try {
            EntryOperator.operator(this).init(this.dataKey, this.oldValue, this.newValue, null, this.modificationType).doPostOperateOps();
        }
        finally {
            this.unlockKey();
        }
    }

    private void verifyLock() {
        if (!this.recordStore.isLockedBy(this.dataKey, this.caller, this.threadId)) {
            throw new EntryOffloadableLockMismatchException(String.format("The key is not locked by the caller=%s and threadId=%d", this.caller, this.threadId));
        }
    }

    private void unlockKey() {
        boolean unlocked = this.recordStore.unlock(this.dataKey, this.caller, this.threadId, this.getCallId());
        if (!unlocked) {
            throw new IllegalStateException(String.format("Unexpected error! EntryOffloadableSetUnlockOperation finished but the unlock method returned false for caller=%s and threadId=%d", this.caller, this.threadId));
        }
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return this.entryBackupProcessor != null ? new EntryBackupOperation(this.name, this.dataKey, this.entryBackupProcessor) : null;
    }

    @Override
    public boolean shouldBackup() {
        return this.mapContainer.getTotalBackupCount() > 0 && this.entryBackupProcessor != null;
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return new LockWaitNotifyKey(this.getServiceNamespace(), this.dataKey);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public int getId() {
        return 136;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.modificationType != null ? this.modificationType.name() : "");
        out.writeData(this.oldValue);
        out.writeData(this.newValue);
        out.writeUTF(this.caller);
        out.writeLong(this.begin);
        out.writeObject(this.entryBackupProcessor);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        String modificationTypeName = in.readUTF();
        this.modificationType = modificationTypeName.equals("") ? null : EntryEventType.valueOf(modificationTypeName);
        this.oldValue = in.readData();
        this.newValue = in.readData();
        this.caller = in.readUTF();
        this.begin = in.readLong();
        this.entryBackupProcessor = (EntryBackupProcessor)in.readObject();
    }
}

