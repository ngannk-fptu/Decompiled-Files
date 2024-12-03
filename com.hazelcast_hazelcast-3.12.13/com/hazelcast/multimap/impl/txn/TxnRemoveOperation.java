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
import com.hazelcast.multimap.impl.txn.TxnRemoveBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class TxnRemoveOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupAwareOperation,
MutatingOperation {
    private long recordId;
    private Data value;
    private transient long startTimeNanos;

    public TxnRemoveOperation() {
    }

    public TxnRemoveOperation(String name, Data dataKey, long recordId, Data value) {
        super(name, dataKey);
        this.recordId = recordId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        this.startTimeNanos = System.nanoTime();
        MultiMapContainer container = this.getOrCreateContainer();
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        if (multiMapValue == null || !multiMapValue.containsRecordId(this.recordId)) {
            this.response = false;
            return;
        }
        this.response = true;
        container.update();
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        Iterator<MultiMapRecord> iter = coll.iterator();
        while (iter.hasNext()) {
            if (iter.next().getRecordId() != this.recordId) continue;
            iter.remove();
            break;
        }
        if (coll.isEmpty()) {
            container.delete(this.dataKey);
        }
    }

    @Override
    public void afterRun() throws Exception {
        long elapsed = Math.max(0L, System.nanoTime() - this.startTimeNanos);
        MultiMapService service = (MultiMapService)this.getService();
        service.getLocalMultiMapStatsImpl(this.name).incrementRemoveLatencyNanos(elapsed);
        if (Boolean.TRUE.equals(this.response)) {
            this.publishEvent(EntryEventType.REMOVED, this.dataKey, null, this.value);
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnRemoveBackupOperation(this.name, this.dataKey, this.recordId, this.value);
    }

    public long getRecordId() {
        return this.recordId;
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
        return 35;
    }
}

