/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.multimap.impl.txn.TxnPutBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.Collection;

public class TxnPutOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupAwareOperation,
MutatingOperation {
    private long recordId;
    private Data value;
    private transient long startTimeNanos;

    public TxnPutOperation() {
    }

    public TxnPutOperation(String name, Data dataKey, Data value, long recordId) {
        super(name, dataKey);
        this.recordId = recordId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        this.startTimeNanos = System.nanoTime();
        MultiMapContainer container = this.getOrCreateContainer();
        MultiMapValue multiMapValue = container.getOrCreateMultiMapValue(this.dataKey);
        if (multiMapValue.containsRecordId(this.recordId)) {
            this.response = false;
            return;
        }
        this.response = true;
        container.update();
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        MultiMapRecord record = new MultiMapRecord(this.recordId, this.isBinary() ? this.value : this.toObject(this.value));
        coll.add(record);
    }

    @Override
    public void afterRun() throws Exception {
        long elapsed = Math.max(0L, System.nanoTime() - this.startTimeNanos);
        MultiMapService service = (MultiMapService)this.getService();
        service.getLocalMultiMapStatsImpl(this.name).incrementPutLatencyNanos(elapsed);
        if (Boolean.TRUE.equals(this.response)) {
            this.publishEvent(EntryEventType.ADDED, this.dataKey, this.value, null);
        }
    }

    public long getRecordId() {
        return this.recordId;
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnPutBackupOperation(this.name, this.dataKey, this.recordId, this.value);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.recordId);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.recordId = in.readLong();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 33;
    }
}

