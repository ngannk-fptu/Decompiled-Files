/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.PutFromLoadAllBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PutFromLoadAllOperation
extends MapOperation
implements PartitionAwareOperation,
MutatingOperation,
BackupAwareOperation {
    private List<Data> keyValueSequence;
    private List<Data> invalidationKeys;

    public PutFromLoadAllOperation() {
        this.keyValueSequence = Collections.emptyList();
    }

    public PutFromLoadAllOperation(String name, List<Data> keyValueSequence) {
        super(name);
        Preconditions.checkFalse(CollectionUtil.isEmpty(keyValueSequence), "key-value sequence cannot be empty or null");
        this.keyValueSequence = keyValueSequence;
    }

    @Override
    public void run() throws Exception {
        boolean hasInterceptor = this.mapServiceContext.hasInterceptor(this.name);
        List<Data> keyValueSequence = this.keyValueSequence;
        for (int i = 0; i < keyValueSequence.size(); i += 2) {
            Data key = keyValueSequence.get(i);
            Data dataValue = keyValueSequence.get(i + 1);
            Preconditions.checkNotNull(key, "Key loaded by a MapLoader cannot be null.");
            Data value = hasInterceptor ? this.mapServiceContext.toObject(dataValue) : dataValue;
            this.recordStore.putFromLoad(key, value, this.getCallerAddress());
            if (value != null && !this.recordStore.existInMemory(key)) continue;
            if (value != null) {
                this.callAfterPutInterceptors(value);
            }
            if (this.isPostProcessing(this.recordStore)) {
                Object record = this.recordStore.getRecord(key);
                Preconditions.checkNotNull(record, "Value loaded by a MapLoader cannot be null.");
                value = record.getValue();
            }
            this.publishLoadAsWanUpdate(key, value);
            this.addInvalidation(key);
        }
    }

    private void addInvalidation(Data key) {
        if (!this.mapContainer.hasInvalidationListener()) {
            return;
        }
        if (this.invalidationKeys == null) {
            this.invalidationKeys = new ArrayList<Data>(this.keyValueSequence.size() / 2);
        }
        this.invalidationKeys.add(key);
    }

    private void callAfterPutInterceptors(Object value) {
        this.mapService.getMapServiceContext().interceptAfterPut(this.name, value);
    }

    @Override
    public void afterRun() throws Exception {
        this.invalidateNearCache(this.invalidationKeys);
        this.evict(null);
        super.afterRun();
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public boolean shouldBackup() {
        return !this.keyValueSequence.isEmpty();
    }

    @Override
    public final int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public final int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public Operation getBackupOperation() {
        return new PutFromLoadAllBackupOperation(this.name, this.keyValueSequence);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        List<Data> keyValueSequence = this.keyValueSequence;
        int size = keyValueSequence.size();
        out.writeInt(size);
        for (Data data : keyValueSequence) {
            out.writeData(data);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        if (size < 1) {
            this.keyValueSequence = Collections.emptyList();
        } else {
            ArrayList<Data> tmpKeyValueSequence = new ArrayList<Data>(size);
            for (int i = 0; i < size; ++i) {
                Data data = in.readData();
                tmpKeyValueSequence.add(data);
            }
            this.keyValueSequence = tmpKeyValueSequence;
        }
    }

    @Override
    public int getId() {
        return 57;
    }
}

