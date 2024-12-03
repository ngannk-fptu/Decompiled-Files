/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.operations.PutBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class PutOperation
extends AbstractBackupAwareMultiMapOperation
implements MutatingOperation {
    private Data value;
    private int index = -1;
    private long recordId;

    public PutOperation() {
    }

    public PutOperation(String name, Data dataKey, long threadId, Data value, int index) {
        super(name, dataKey, threadId);
        this.value = value;
        this.index = index;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        this.recordId = container.nextId();
        MultiMapRecord record = new MultiMapRecord(this.recordId, this.isBinary() ? this.value : this.toObject(this.value));
        Collection<MultiMapRecord> coll = container.getOrCreateMultiMapValue(this.dataKey).getCollection(false);
        if (this.index == -1) {
            this.response = coll.add(record);
        } else {
            try {
                ((List)coll).add(this.index, record);
                this.response = true;
            }
            catch (IndexOutOfBoundsException e) {
                this.response = e;
            }
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (Boolean.TRUE.equals(this.response)) {
            this.getOrCreateContainer().update();
            this.publishEvent(EntryEventType.ADDED, this.dataKey, this.value, null);
        }
    }

    @Override
    public Operation getBackupOperation() {
        return new PutBackupOperation(this.name, this.dataKey, this.value, this.recordId, this.index);
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.index);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.index = in.readInt();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 16;
    }
}

