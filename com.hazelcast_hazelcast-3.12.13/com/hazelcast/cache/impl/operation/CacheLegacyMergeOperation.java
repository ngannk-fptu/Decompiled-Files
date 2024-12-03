/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.cache.impl.operation.CachePutBackupOperation;
import com.hazelcast.cache.impl.operation.KeyBasedCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.wan.impl.CallerProvenance;
import java.io.IOException;

public class CacheLegacyMergeOperation
extends KeyBasedCacheOperation
implements BackupAwareOperation {
    private CacheMergePolicy mergePolicy;
    private CacheEntryView<Data, Data> mergingEntry;

    public CacheLegacyMergeOperation() {
    }

    public CacheLegacyMergeOperation(String name, Data key, CacheEntryView<Data, Data> entryView, CacheMergePolicy policy) {
        super(name, key);
        this.mergingEntry = entryView;
        this.mergePolicy = policy;
    }

    @Override
    public void run() throws Exception {
        this.backupRecord = this.recordStore.merge(this.mergingEntry, this.mergePolicy, "<NA>", null, -1, CallerProvenance.NOT_WAN);
    }

    @Override
    public void afterRun() {
        if (this.backupRecord != null) {
            this.publishWanUpdate(this.key, this.backupRecord);
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.backupRecord != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutBackupOperation(this.name, this.key, this.backupRecord);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.mergingEntry);
        out.writeObject(this.mergePolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergingEntry = (CacheEntryView)in.readObject();
        this.mergePolicy = (CacheMergePolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 38;
    }
}

