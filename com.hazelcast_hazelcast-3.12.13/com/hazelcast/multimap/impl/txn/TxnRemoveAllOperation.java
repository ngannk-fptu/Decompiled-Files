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
import com.hazelcast.multimap.impl.txn.TxnRemoveAllBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class TxnRemoveAllOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupAwareOperation,
MutatingOperation {
    private Collection<Long> recordIds;
    private transient long startTimeNanos;
    private transient Collection<MultiMapRecord> removed;

    public TxnRemoveAllOperation() {
    }

    public TxnRemoveAllOperation(String name, Data dataKey, Collection<MultiMapRecord> records) {
        super(name, dataKey);
        this.recordIds = new ArrayList<Long>();
        for (MultiMapRecord record : records) {
            this.recordIds.add(record.getRecordId());
        }
    }

    @Override
    public void run() throws Exception {
        this.startTimeNanos = System.nanoTime();
        MultiMapContainer container = this.getOrCreateContainer();
        MultiMapValue multiMapValue = container.getOrCreateMultiMapValue(this.dataKey);
        for (Long recordId : this.recordIds) {
            if (multiMapValue.containsRecordId(recordId)) continue;
            this.response = false;
            return;
        }
        this.response = true;
        container.update();
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        this.removed = new LinkedList<MultiMapRecord>();
        block1: for (Long recordId : this.recordIds) {
            Iterator<MultiMapRecord> iter = coll.iterator();
            while (iter.hasNext()) {
                MultiMapRecord record = iter.next();
                if (record.getRecordId() != recordId.longValue()) continue;
                iter.remove();
                this.removed.add(record);
                continue block1;
            }
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
        if (this.removed != null) {
            for (MultiMapRecord record : this.removed) {
                this.publishEvent(EntryEventType.REMOVED, this.dataKey, null, record.getObject());
            }
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnRemoveAllBackupOperation(this.name, this.dataKey, this.recordIds);
    }

    public Collection<Long> getRecordIds() {
        return this.recordIds;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.recordIds.size());
        for (Long recordId : this.recordIds) {
            out.writeLong(recordId);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.recordIds = new ArrayList<Long>();
        for (int i = 0; i < size; ++i) {
            this.recordIds.add(in.readLong());
        }
    }

    @Override
    public int getId() {
        return 37;
    }
}

